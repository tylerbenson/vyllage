package editor.repository;

import static editor.domain.editor.tables.Accounts.ACCOUNTS;

import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import editor.domain.editor.tables.records.AccountsRecord;
import editor.model.Account;

@Repository
public class AccountRepository {

	private final Logger logger = Logger.getLogger(AccountRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	public Account get(Long accountid) {
		AccountsRecord record = sql.fetchOne(ACCOUNTS,
				ACCOUNTS.ID.eq(accountid));

		if (record != null)
			logger.info("Found account.");

		Account account = new Account();
		account.setId(accountid);
		account.setUserName(record.getUsername());
		return account;
	}

}
