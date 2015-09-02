package accounts.repository;

import static accounts.domain.tables.Emails.EMAILS;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.EmailsRecord;
import accounts.model.Email;

@Repository
public class EmailRepository {

	@Autowired
	private DSLContext sql;

	public List<Email> getByUserId(Long userId) {

		Result<EmailsRecord> fetch = sql.fetch(EMAILS,
				EMAILS.USER_ID.eq(userId));

		if (fetch == null || fetch.isEmpty())
			return Collections.emptyList();

		return fetch.stream().map(r -> {
			Email email = new Email();
			email.setConfirmed(r.getConfirmed());
			email.setDateCreated(r.getDateCreated().toLocalDateTime());
			email.setDefaultEmail(r.getDefaultEmail());
			email.setEmailId(r.getEmailId());
			email.setLastModified(r.getLastModified().toLocalDateTime());
			email.setUserId(r.getUserId());
			return email;
		}).collect(Collectors.toList());
	}

}
