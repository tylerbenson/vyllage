package accounts.repository;

import static accounts.domain.tables.AccountSetting.ACCOUNT_SETTING;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.AccountSettingRecord;
import accounts.model.User;
import accounts.model.account.settings.AccountSetting;

@Repository
public class AccountSettingRepository {

	@Autowired
	private DSLContext sql;

	public List<AccountSetting> getAccountSettings(User user) {
		List<Record> result = sql.select().from(ACCOUNT_SETTING)
				.where(ACCOUNT_SETTING.USER_ID.eq(user.getUserId())).fetch();

		return result
				.stream()
				.map(r -> new AccountSetting(r
						.getValue(ACCOUNT_SETTING.ACCOUNT_SETTING_ID), r
						.getValue(ACCOUNT_SETTING.USER_ID), r
						.getValue(ACCOUNT_SETTING.NAME), r
						.getValue(ACCOUNT_SETTING.VALUE), r
						.getValue(ACCOUNT_SETTING.PRIVACY)))
				.collect(Collectors.toList());
	}

	public AccountSetting get(Long userId, String settingName) {
		AccountSetting setting = new AccountSetting();

		AccountSettingRecord settingRecord = sql.fetchOne(
				ACCOUNT_SETTING,
				ACCOUNT_SETTING.USER_ID.eq(userId).and(
						ACCOUNT_SETTING.NAME.eq(settingName)));

		setting.setAccountSettingId(settingRecord.getAccountSettingId());
		setting.setName(settingRecord.getName());
		setting.setPrivacy(settingRecord.getPrivacy());
		setting.setValue(settingRecord.getValue());
		setting.setUserId(settingRecord.getUserId());
		return setting;
	}

	public void set(Long userId, AccountSetting setting) {
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
		} else {
			settingRecord.setPrivacy(setting.getPrivacy());
			settingRecord.setValue(setting.getValue());
			settingRecord.store();
		}
	}
}
