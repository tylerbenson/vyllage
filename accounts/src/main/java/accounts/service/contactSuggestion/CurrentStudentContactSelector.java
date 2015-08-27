package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.domain.tables.UserOrganizationRoles;
import accounts.domain.tables.Users;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.UserOrganizationRoleRepository;
import accounts.service.AccountSettingsService;

public class CurrentStudentContactSelector extends AbstractContactSelector {

	public static final int DAYS_NEAR_GRADUATION_DATE = 35;

	private static final String MMM_YYYY_DD = "MMM yyyy dd";

	private AccountSettingsService accountSettingsService;

	public CurrentStudentContactSelector(DSLContext sql,
			UserOrganizationRoleRepository userOrganizationRoleRepository,
			AccountSettingsService accountSettingsService) {
		super(sql, userOrganizationRoleRepository);
		this.accountSettingsService = accountSettingsService;
	}

	@Override
	protected SelectConditionStep<Record> getSuggestions(User user) {

		Users u = USERS.as("u");
		UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");

		// determine if it's close to graduation or already graduated
		if (isNearGraduationDate(user)) {
			// if it is, suggest career advisors & transfer advisors

			return sql()
					.select(u.fields())
					.from(u)
					.join(uor)
					.on(u.USER_ID.eq(uor.USER_ID))
					.where(uor.ORGANIZATION_ID.in(user
							.getAuthorities()
							.stream()
							.map(a -> ((UserOrganizationRole) a)
									.getOrganizationId())
							.collect(Collectors.toList())))
					.and(nearGraduationSearchCondition(uor))
					.and(u.ENABLED.eq(true));

		}

		// if not, suggest academic advisors
		return sql()
				.select(u.fields())
				.from(u)
				.join(uor)
				.on(u.USER_ID.eq(uor.USER_ID))
				.where(uor.ORGANIZATION_ID.in(user
						.getAuthorities()
						.stream()
						.map(a -> ((UserOrganizationRole) a)
								.getOrganizationId())
						.collect(Collectors.toList())))
				.and(studentSearchCondition(uor)).and(u.ENABLED.eq(true));

	}

	private Condition nearGraduationSearchCondition(UserOrganizationRoles uor) {
		return uor.ROLE.contains(RolesEnum.CAREER_ADVISOR.name()).or(
				uor.ROLE.contains(RolesEnum.TRANSFER_ADVISOR.name()));
	}

	private Condition studentSearchCondition(UserOrganizationRoles uor) {
		return uor.ROLE.contains(RolesEnum.ACADEMIC_ADVISOR.name());
	}

	protected boolean isNearGraduationDate(User user) {

		boolean isWithinGraduationDateRange = false;
		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, "graduationDate");

		if (accountSetting.isPresent()) {
			LocalDate graduationDate = null;

			try {

				graduationDate = LocalDate.parse(accountSetting.get()
						.getValue() + " 01",
						DateTimeFormatter.ofPattern(MMM_YYYY_DD));

			} catch (java.time.format.DateTimeParseException e) {

				graduationDate = LocalDate.parse(accountSetting.get()
						.getValue());
			}

			isWithinGraduationDateRange = LocalDate.now().isAfter(
					graduationDate.minusDays(DAYS_NEAR_GRADUATION_DATE));
		}

		return isWithinGraduationDateRange;
	}

	@Override
	public List<User> backfill(User user, int limit) {
		Users u = USERS.as("u");
		UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");

		List<User> recordsToUser = new ArrayList<>();

		recordsToUser.addAll(recordsToUser(sql()
				.select(u.fields())
				.from(u)
				.join(uor)
				.on(u.USER_ID.eq(uor.USER_ID))
				.where(uor.ORGANIZATION_ID.in(user
						.getAuthorities()
						.stream()
						.map(a -> ((UserOrganizationRole) a)
								.getOrganizationId())
						.collect(Collectors.toList())))
				.and(uor.ROLE.contains(RolesEnum.ADVISOR.name()))
				.and(u.ENABLED.eq(true)).limit(limit).fetch()));

		// still not enough, we add students
		if (recordsToUser == null || recordsToUser.isEmpty()
				|| recordsToUser.size() < limit)
			recordsToUser.addAll(recordsToUser(sql()
					.select(u.fields())
					.from(u)
					.join(uor)
					.on(u.USER_ID.eq(uor.USER_ID))
					.where(uor.ORGANIZATION_ID.in(user
							.getAuthorities()
							.stream()
							.map(a -> ((UserOrganizationRole) a)
									.getOrganizationId())
							.collect(Collectors.toList())))
					.and(uor.ROLE.contains(RolesEnum.STUDENT.name()))
					.and(u.ENABLED.eq(true)).limit(limit).fetch()));

		return recordsToUser;
	}

}
