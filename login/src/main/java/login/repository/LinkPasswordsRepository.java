package login.repository;

import static login.domain.tables.LinkPasswords.LINK_PASSWORDS;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import login.domain.tables.records.LinkPasswordsRecord;
import login.model.link.DocumentLink;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

/**
 * Handles links to documents. A user can generate links to share his documents
 * to other users.
 * 
 * @author uh
 *
 */
@Repository
public class LinkPasswordsRepository {

	@Autowired
	private DSLContext sql;

	public void create(DocumentLink linkRequest, LocalDateTime expires) {
		LinkPasswordsRecord newRecord = sql.newRecord(LINK_PASSWORDS);
		newRecord.setPassword(new BCryptPasswordEncoder().encode(linkRequest
				.getGeneratedPassword()));
		newRecord.setEnabled(true);
		newRecord.setUsername(linkRequest.getUserName());
		newRecord.setDocumentid(linkRequest.getDocumentId());
		newRecord.setExpires(Timestamp.valueOf(expires));
		newRecord.setDocumenttype(linkRequest.getDocumentType());
		newRecord.insert();
	}

}
