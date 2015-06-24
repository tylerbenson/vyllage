package accounts.repository;

import static accounts.domain.tables.SharedDocument.SHARED_DOCUMENT;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.SharedDocumentRecord;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.LinkType;
import accounts.model.link.SocialDocumentLink;

@Repository
public class SharedDocumentRepository {

	@Autowired
	private DSLContext sql;

	@Autowired
	private TextEncryptor textEncryptor;

	public void create(EmailDocumentLink doclink) {
		SharedDocumentRecord sharedDocumentRecord = sql
				.newRecord(SHARED_DOCUMENT);

		sharedDocumentRecord.setShortUrl(doclink.getDocumentURL());
		sharedDocumentRecord.setLinkType(LinkType.EMAIL.name());
		sharedDocumentRecord.setDocumentId(doclink.getDocumentId());
		sharedDocumentRecord.setDocumentType(doclink.getDocumentType());
		// handled by user credentials
		sharedDocumentRecord.setExpirationDate(null);

		sharedDocumentRecord.setGeneratedPassword(textEncryptor.encrypt(doclink
				.getGeneratedPassword()));

		sharedDocumentRecord.setUserId(doclink.getUserId());
		sharedDocumentRecord.setDateCreated(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));
		sharedDocumentRecord.insert();
	}

	public void create(SocialDocumentLink doclink) {
		SharedDocumentRecord sharedDocumentRecord = sql
				.newRecord(SHARED_DOCUMENT);
		sharedDocumentRecord.setShortUrl(doclink.getDocumentURL());
		sharedDocumentRecord.setLinkType(LinkType.SOCIAL.name());
		sharedDocumentRecord.setDocumentId(doclink.getDocumentId());
		sharedDocumentRecord.setDocumentType(doclink.getDocumentType());
		sharedDocumentRecord.setExpirationDate(Timestamp.valueOf(doclink
				.getExpirationDate()));
		sharedDocumentRecord.setUserId(doclink.getUserId());
		sharedDocumentRecord.setDateCreated(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));
		sharedDocumentRecord.insert();

	}

	public EmailDocumentLink getDocumentLink(String shortUrl) {
		SharedDocumentRecord sharedDocumentRecord = sql.fetchOne(
				SHARED_DOCUMENT, SHARED_DOCUMENT.SHORT_URL.eq(shortUrl));
		EmailDocumentLink documentLink = new EmailDocumentLink();

		documentLink.setDocumentId(sharedDocumentRecord.getDocumentId());
		documentLink.setDocumentType(sharedDocumentRecord.getDocumentType());
		documentLink.setLinkType(LinkType.EMAIL);
		documentLink.setDocumentURL(sharedDocumentRecord.getShortUrl());
		documentLink.setGeneratedPassword(textEncryptor
				.decrypt(sharedDocumentRecord.getGeneratedPassword()));
		documentLink.setUserId(sharedDocumentRecord.getUserId());
		documentLink.setVisits(sharedDocumentRecord.getVisits());

		return documentLink;
	}

	public SocialDocumentLink getSimpleDocumentLink(String shortUrl) {
		return new SocialDocumentLink(sql.fetchOne(SHARED_DOCUMENT,
				SHARED_DOCUMENT.SHORT_URL.eq(shortUrl)));
	}

	public void registerVisit(String shortUrl) {
		SharedDocumentRecord sharedDocumentRecord = sql.fetchOne(
				SHARED_DOCUMENT, SHARED_DOCUMENT.SHORT_URL.eq(shortUrl));

		Long visits = sharedDocumentRecord.getVisits();
		sharedDocumentRecord.setVisits(visits == null ? 1L : visits + 1);
		sharedDocumentRecord.insert();
	}

}
