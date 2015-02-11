package editor.repository;

import static editor.domain.editor.tables.Accounts.ACCOUNTS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import editor.domain.editor.tables.records.AccountsRecord;
import editor.model.Account;

@Repository
public class AccountRepository implements IRepository<Account> {

	private final Logger logger = Logger.getLogger(AccountRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public Account get(Long accountid) {
		AccountsRecord record = sql.fetchOne(ACCOUNTS,
				ACCOUNTS.ID.eq(accountid));

		Account account = recordToAccount(record);
		return account;
	}

	@Override
	public List<Account> getAll() {
		Result<AccountsRecord> all = sql.fetch(ACCOUNTS);
		List<Account> allAccounts = new ArrayList<>();

		for (AccountsRecord accountsRecord : all)
			allAccounts.add(recordToAccount(accountsRecord));
		return allAccounts;
	}

	@Override
	public Account save(Account account) {

		AccountsRecord existingRecord = sql.fetchOne(ACCOUNTS,
				ACCOUNTS.ID.eq(account.getId()));

		if (existingRecord == null) {
			AccountsRecord newRecord = sql.newRecord(ACCOUNTS);
			newRecord.setUsername(account.getUserName());

			newRecord.store();
			account.setId(newRecord.getId());

		} else {

			existingRecord.setUsername(account.getUserName());
			existingRecord.update();
		}

		logger.info("Saved account: " + account);

		return account;
	}

	public void delete(Account account) {
		this.delete(account.getId());
	}

	@Override
	public void delete(Long accountId) {
		AccountsRecord existingRecord = sql.fetchOne(ACCOUNTS,
				ACCOUNTS.ID.eq(accountId));
		existingRecord.delete();
	}

	private Account recordToAccount(AccountsRecord accountsRecord) {
		Account account = new Account();
		account.setId(accountsRecord.getId());
		account.setUserName(accountsRecord.getUsername());
		return account;
	}

}
