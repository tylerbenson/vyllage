package accounts.repository;

import static accounts.domain.tables.Emails.EMAILS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import accounts.domain.tables.records.EmailsRecord;
import accounts.model.Email;

@Repository
public class EmailRepository {

	@Autowired
	private DSLContext sql;

	public List<Email> getByUserId(Long userId) {

		return sql.fetch(EMAILS, EMAILS.USER_ID.eq(userId)).into(Email.class);
	}

	public Email save(Email email) {

		EmailsRecord result = sql.fetchOne(EMAILS,
				EMAILS.EMAIL.eq(email.getEmail()));

		if (result == null) {
			EmailsRecord newRecord = sql.newRecord(EMAILS, email);

			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			newRecord.store();

			Assert.notNull(newRecord.getEmailId());
			email.setEmailId(newRecord.getEmailId());

			return email;

		} else {

			result.from(email);

			result.setLastModified(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));

			result.update();
			return email;
		}
	}

	public Optional<Email> getByEmail(String emailAddress) {
		EmailsRecord record = sql.fetchOne(EMAILS,
				EMAILS.EMAIL.eq(emailAddress));

		if (record == null)
			return Optional.empty();

		Email email = new Email();
		email.setConfirmed(record.getConfirmed());
		email.setDefaultEmail(record.getDefaultEmail());
		email.setEmail(record.getEmail());
		email.setEmailId(record.getEmailId());
		email.setUserId(record.getUserId());

		return Optional.of(email);
	}

}
