package accounts.repository;

import static accounts.domain.tables.AccountSetting.ACCOUNT_SETTING;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import user.common.User;
import accounts.domain.tables.records.AccountSettingRecord;
import accounts.model.account.settings.AccountSetting;

@Repository
public class AccountSettingRepository {

	@Autowired
	private DSLContext sql;

	public List<AccountSetting> getAccountSettings(User user) {
		List<Record> result = sql.select().from(ACCOUNT_SETTING)
				.where(ACCOUNT_SETTING.USER_ID.eq(user.getUserId())).fetch();

		return result.stream().map(createAccountSetting())
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves account settings based on user user ids.
	 * 
	 * @param User
	 *            ids
	 * @return
	 */
	public List<AccountSetting> getAccountSettings(List<Long> ids) {
		List<Record> result = sql.select().from(ACCOUNT_SETTING)
				.where(ACCOUNT_SETTING.USER_ID.in(ids)).fetch();

		return result.stream().map(createAccountSetting())
				.collect(Collectors.toList());
	}

	public List<AccountSetting> get(Long userId, String settingName)
			throws ElementNotFoundException {

		Result<AccountSettingRecord> settingRecords = sql.fetch(
				ACCOUNT_SETTING,
				ACCOUNT_SETTING.USER_ID.eq(userId).and(
						ACCOUNT_SETTING.NAME.eq(settingName)));

		if (settingRecords == null || settingRecords.isEmpty())
			throw new ElementNotFoundException("The setting with name '"
					+ settingName + "' for User with id '" + userId
					+ "' could not be found.");

		return settingRecords.stream().map(createAccountSetting())
				.collect(Collectors.toList());
	}

	public AccountSetting set(Long userId, AccountSetting setting) {
		AccountSettingRecord settingRecord = sql.fetchOne(
				ACCOUNT_SETTING,
				ACCOUNT_SETTING.USER_ID.eq(userId).and(
						ACCOUNT_SETTING.NAME.eq(setting.getName())));

		if (settingRecord == null) {
			AccountSettingRecord newRecord = sql.newRecord(ACCOUNT_SETTING);
			newRecord.setName(setting.getName());
			newRecord.setPrivacy(setting.getPrivacy());
			newRecord.setUserId(userId);
			newRecord.setValue(setting.getValue());
			newRecord.store();
			setting.setAccountSettingId(newRecord.getAccountSettingId());
		} else {
			settingRecord.setPrivacy(setting.getPrivacy());
			settingRecord.setValue(setting.getValue());
			settingRecord.store();
		}

		return setting;
	}

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
