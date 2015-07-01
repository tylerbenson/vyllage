package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.domain.tables.UserOrganizationRoles;
import accounts.domain.tables.Users;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.ElementNotFoundException;
import accounts.repository.UserOrganizationRoleRepository;
import accounts.service.AccountSettingsService;

public class CurrentStudentContactSelector extends AbstractContactSelector {

	public static final int DAYS_NEAR_GRADUATION_DATE = 35;

	private AccountSettingsService accountSettingsService;

	public CurrentStudentContactSelector(DSLContext sql,
			UserOrganizationRoleRepository userOrganizationRoleRepository,
			AccountSettingsService accountSettingsService) {
		super(sql, userOrganizationRoleRepository);
		this.accountSettingsService = accountSettingsService;
	}

	@Override
	protected Optional<SelectConditionStep<Record>> getSuggestions(User user) {

		// check if it's a student or alumni
		if (isStudentOrAlumni(user)) {
			Users u = USERS.as("u");
			UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");

			// determine if it's close to graduation or already graduated
			if (isAlumniOrNearGraduationDate(user)) {
				// if it is, suggest career advisors & transfer advisors

				SelectConditionStep<Record> select = sql()
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
						.and(uor.ROLE.contains(RolesEnum.CAREER_ADVISOR.name())
								.or(uor.ROLE
										.contains(RolesEnum.TRANSFER_ADVISOR
												.name())));

				return Optional.of(select);
			}

			// if not, suggest academic advisors
			SelectConditionStep<Record> select = sql()
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
					.and(uor.ROLE.contains(RolesEnum.ACADEMIC_ADVISOR.name()));

			return Optional.of(select);
		}
		return Optional.empty();
	}

	public boolean isAlumniOrNearGraduationDate(User user) {

		boolean isWithinGraduationDateRange = false;
		boolean isAlumni = user
				.getAuthorities()
				.stream()
				.anyMatch(
						a -> a.getAuthority().contains(RolesEnum.ALUMNI.name()));
		try {
			List<AccountSetting> settings = accountSettingsService
					.getAccountSetting(user, "graduationDate");
			LocalDateTime graduationDate = LocalDateTime.parse(settings.get(0)
					.getValue());
			isWithinGraduationDateRange = LocalDateTime.now().isAfter(
					graduationDate.minusDays(DAYS_NEAR_GRADUATION_DATE));

		} catch (ElementNotFoundException e) {
			// nothing to do
		}

		return isAlumni || isWithinGraduationDateRange;
	}

	public boolean isStudentOrAlumni(User user) {
		return user
				.getAuthorities()
				.stream()
				.anyMatch(
						a -> a.getAuthority()
								.contains(RolesEnum.STUDENT.name())
								|| a.getAuthority().contains(
										RolesEnum.ALUMNI.name()));
	}

	@Override
	protected void applyFilters(
			SelectConditionStep<Record> selectConditionStep,
			Map<String, String> filters) {
		// nothing right now
	}

}
