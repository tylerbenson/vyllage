package accounts.repository;

import static accounts.domain.tables.SharedDocument.SHARED_DOCUMENT;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.SharedDocumentRecord;
import accounts.model.link.SimpleDocumentLink;

@Repository
public class SharedDocumentRepository {

	@Autowired
	private DSLContext sql;

	public void create(SimpleDocumentLink doclink) {
		SharedDocumentRecord sharedDocumentRecord = sql
				.newRecord(SHARED_DOCUMENT);
		sharedDocumentRecord.setShortUrl(doclink.getDocumentURL());
		sharedDocumentRecord.setLinkType(doclink.getLinkType().name());
		sharedDocumentRecord.setDocumentId(doclink.getDocumentId());
		sharedDocumentRecord.setDocumentType(doclink.getDocumentType());
		sharedDocumentRecord.setExpirationDate(Timestamp.valueOf(doclink
				.getExpirationDate()));
		sharedDocumentRecord.setUserId(doclink.getUserId());
		sharedDocumentRecord.setDateCreated(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));
		sharedDocumentRecord.insert();

	}

	public SimpleDocumentLink get(String shortUrl) {
		return new SimpleDocumentLink(sql.fetchOne(SHARED_DOCUMENT));
	}

}
