package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.time.LocalDateTime;
import java.util.List;
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
					.and(nearGraduationSearchCondition(uor));

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
				.and(studentSearchCondition(uor));

	}

	private Condition nearGraduationSearchCondition(UserOrganizationRoles uor) {
		return uor.ROLE.contains(RolesEnum.CAREER_ADVISOR.name()).or(
				uor.ROLE.contains(RolesEnum.TRANSFER_ADVISOR.name()));
	}

	private Condition studentSearchCondition(UserOrganizationRoles uor) {
		return uor.ROLE.contains(RolesEnum.ACADEMIC_ADVISOR.name());
	}

	public boolean isNearGraduationDate(User user) {

		boolean isWithinGraduationDateRange = false;
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

		return isWithinGraduationDateRange;
	}

}
