package accounts.repository;

import static accounts.domain.tables.AccountSetting.ACCOUNT_SETTING;
import static accounts.domain.tables.Users.USERS;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import user.common.User;
import accounts.domain.tables.Users;
import accounts.domain.tables.records.AccountSettingRecord;
import accounts.model.account.settings.AccountSetting;

import com.newrelic.api.agent.NewRelic;

@Repository
public class AccountSettingRepository {

	private final Logger logger = Logger
			.getLogger(AccountSettingRepository.class.getName());

	@Autowired
	private DSLContext sql;

	public List<AccountSetting> getAccountSettings(User user) {
		List<Record> result = sql.select().from(ACCOUNT_SETTING)
				.where(ACCOUNT_SETTING.USER_ID.eq(user.getUserId())).fetch();

		return result.stream().map(createAccountSetting())
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves account settings based on user ids.
	 *
	 * @param User
	 *            ids
	 * @return
	 */
	public List<AccountSetting> getAccountSettings(List<Long> ids) {
		accounts.domain.tables.AccountSetting acs = ACCOUNT_SETTING.as("acs");
		Users u = USERS.as("u");

		List<Record> result = sql.select(acs.fields()).from(acs).join(u)
				.on(acs.USER_ID.eq(u.USER_ID)).where(acs.USER_ID.in(ids))
				.and(u.ENABLED.eq(true)).fetch();

		return result.stream().map(createAccountSetting())
				.collect(Collectors.toList());
	}

	public Optional<AccountSetting> get(Long userId, String settingName) {
		accounts.domain.tables.AccountSetting acs = ACCOUNT_SETTING.as("acs");
		Users u = USERS.as("u");

		Record settingRecord = sql.select(acs.fields()).from(acs).join(u)
				.on(acs.USER_ID.eq(u.USER_ID)).where(acs.USER_ID.in(userId))
				.and(acs.NAME.eq(settingName)).and(u.ENABLED.eq(true))
				.fetchOne();

		if (settingRecord == null)
			return Optional.empty();

		return Optional.ofNullable(createAccountSetting().apply(settingRecord));
	}

	public AccountSetting set(Long userId, AccountSetting setting) {

		logger.info("Preparing to save setting: " + setting);

		AccountSettingRecord settingRecord = sql.fetchOne(
				ACCOUNT_SETTING,
				ACCOUNT_SETTING.USER_ID.eq(userId).and(
						ACCOUNT_SETTING.NAME.eq(setting.getName())));

		try {

			if (settingRecord == null) {
				AccountSettingRecord newRecord = sql.newRecord(ACCOUNT_SETTING);
				newRecord.setName(setting.getName());
				newRecord.setPrivacy(setting.getPrivacy());
				newRecord.setUserId(userId);
				newRecord.setValue(setting.getValue());
				newRecord.store();
				setting.setAccountSettingId(newRecord.getAccountSettingId());
				logger.info("Created Setting: " + setting);
			} else {
				settingRecord.setPrivacy(setting.getPrivacy());
				settingRecord.setValue(setting.getValue());
				settingRecord.store();
				logger.info("Updated: " + setting);
			}

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return setting;
	}

	/**
	 * Deletes all account settings that belong to the user id.
	 *
	 * @param userId
	 */
	public void deleteByUserId(Long userId) {
		sql.delete(ACCOUNT_SETTING).where(ACCOUNT_SETTING.USER_ID.eq(userId))
				.execute();
	}

	public void deleteByName(Long userId, String name) {
		sql.delete(ACCOUNT_SETTING)
				.where(ACCOUNT_SETTING.USER_ID.eq(userId).and(
						ACCOUNT_SETTING.NAME.eq(name))).execute();
	}

	private Function<? super Record, ? extends AccountSetting> createAccountSetting() {

		return r -> new AccountSetting(
				r.getValue(ACCOUNT_SETTING.ACCOUNT_SETTING_ID),
				r.getValue(ACCOUNT_SETTING.USER_ID),
				r.getValue(ACCOUNT_SETTING.NAME),
				r.getValue(ACCOUNT_SETTING.VALUE),
				r.getValue(ACCOUNT_SETTING.PRIVACY));
	}
}
