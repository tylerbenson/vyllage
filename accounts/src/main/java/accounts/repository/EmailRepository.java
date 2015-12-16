package accounts.repository;

import static accounts.domain.tables.Emails.EMAILS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
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

		Result<EmailsRecord> result = sql.fetch(EMAILS,
				EMAILS.USER_ID.eq(userId));

		if (result == null || result.isEmpty())
			return Collections.emptyList();

		return result
				.stream()
				.map(record -> new Email(record.getEmailId(), record
						.getUserId(), record.getEmail(), record
						.getDefaultEmail(), record.getConfirmed()))
				.collect(Collectors.toList());
	}

	public Email save(Email email) {

		EmailsRecord result = sql.fetchOne(EMAILS,
				EMAILS.EMAIL_ID.eq(email.getEmailId()));

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

		Email email = new Email(record.getEmailId(), record.getUserId(),
				record.getEmail(), record.getDefaultEmail(),
				record.getConfirmed());

		return Optional.of(email);
	}

}
