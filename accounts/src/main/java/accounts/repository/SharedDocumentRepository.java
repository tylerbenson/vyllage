package accounts.repository;

import static accounts.domain.tables.SharedDocument.SHARED_DOCUMENT;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.SharedDocumentRecord;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.LinkType;
import accounts.model.link.SocialDocumentLink;

@Repository
public class SharedDocumentRepository {

	@Autowired
	private DSLContext sql;

	public void create(EmailDocumentLink doclink) {
		SharedDocumentRecord sharedDocumentRecord = sql
				.newRecord(SHARED_DOCUMENT);

		sharedDocumentRecord.setLinkKey(doclink.getLinkKey());
		sharedDocumentRecord.setLinkType(LinkType.EMAIL.name());
		sharedDocumentRecord.setDocumentId(doclink.getDocumentId());
		sharedDocumentRecord.setDocumentType(doclink.getDocumentType());
		sharedDocumentRecord.setExpirationDate(Timestamp.valueOf(doclink
				.getExpirationDate()));

		sharedDocumentRecord.setUserId(doclink.getUserId());
		sharedDocumentRecord.setDateCreated(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));
		sharedDocumentRecord.setVisits(0L);
		sharedDocumentRecord.insert();
	}

	public void create(SocialDocumentLink doclink) {
		SharedDocumentRecord sharedDocumentRecord = sql
				.newRecord(SHARED_DOCUMENT);
		sharedDocumentRecord.setLinkKey(doclink.getLinkKey());
		sharedDocumentRecord.setLinkType(LinkType.SOCIAL.name());
		sharedDocumentRecord.setDocumentId(doclink.getDocumentId());
		sharedDocumentRecord.setDocumentType(doclink.getDocumentType());
		sharedDocumentRecord.setExpirationDate(Timestamp.valueOf(doclink
				.getExpirationDate()));
		sharedDocumentRecord.setUserId(doclink.getUserId());
		sharedDocumentRecord.setDateCreated(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));
		sharedDocumentRecord.setVisits(0L);
		sharedDocumentRecord.insert();

	}

	public EmailDocumentLink getEmailDocumentLink(String linkKey) {
		SharedDocumentRecord sharedDocumentRecord = sql.fetchOne(
				SHARED_DOCUMENT, SHARED_DOCUMENT.LINK_KEY.eq(linkKey));

		return buildEmailDocumentLink(sharedDocumentRecord);
	}

	public SocialDocumentLink getSocialDocumentLink(String linkKey) {
		SharedDocumentRecord sharedDocumentRecord = sql.fetchOne(
				SHARED_DOCUMENT, SHARED_DOCUMENT.LINK_KEY.eq(linkKey));

		return buildSocialDocumentLink(sharedDocumentRecord);

	}

	public void registerVisit(String linkKey) {
		SharedDocumentRecord sharedDocumentRecord = sql.fetchOne(
				SHARED_DOCUMENT, SHARED_DOCUMENT.LINK_KEY.eq(linkKey));

		Long visits = sharedDocumentRecord.getVisits();
		sharedDocumentRecord.setVisits(visits == null ? 1L : visits + 1);
		sharedDocumentRecord.update();
	}

	public List<SocialDocumentLink> getAllSocialDocumentLinks() {
		Result<SharedDocumentRecord> result = sql.fetch(SHARED_DOCUMENT,
				SHARED_DOCUMENT.LINK_TYPE.eq(LinkType.SOCIAL.name()));

		return result.stream().map(this::buildSocialDocumentLink)
				.collect(Collectors.toList());
	}

	public List<EmailDocumentLink> getAllEmailDocumentLinks() {
		Result<SharedDocumentRecord> result = sql.fetch(SHARED_DOCUMENT,
				SHARED_DOCUMENT.LINK_TYPE.eq(LinkType.EMAIL.name()));

		return result.stream().map(this::buildEmailDocumentLink)
				.collect(Collectors.toList());
	}

	protected EmailDocumentLink buildEmailDocumentLink(
			SharedDocumentRecord sharedDocumentRecord) {
		EmailDocumentLink documentLink = new EmailDocumentLink();

		documentLink.setDocumentId(sharedDocumentRecord.getDocumentId());
		documentLink.setDocumentType(sharedDocumentRecord.getDocumentType());
		documentLink.setLinkType(LinkType.valueOf(sharedDocumentRecord
				.getLinkType()));
		documentLink.setExpirationDate(sharedDocumentRecord.getExpirationDate()
				.toLocalDateTime());
		documentLink.setLinkKey(sharedDocumentRecord.getLinkKey());
		documentLink.setUserId(sharedDocumentRecord.getUserId());
		documentLink.setVisits(sharedDocumentRecord.getVisits() == null ? 0L
				: sharedDocumentRecord.getVisits());
		return documentLink;
	}

	protected SocialDocumentLink buildSocialDocumentLink(
			SharedDocumentRecord sharedDocumentRecord) {
		SocialDocumentLink documentLink = new SocialDocumentLink();

		documentLink.setDocumentId(sharedDocumentRecord.getDocumentId());
		documentLink.setDocumentType(sharedDocumentRecord.getDocumentType());
		documentLink.setLinkType(LinkType.valueOf(sharedDocumentRecord
				.getLinkType()));
		documentLink.setLinkKey(sharedDocumentRecord.getLinkKey());
		documentLink.setUserId(sharedDocumentRecord.getUserId());
		documentLink.setExpirationDate(sharedDocumentRecord.getExpirationDate()
				.toLocalDateTime());
		documentLink.setVisits(sharedDocumentRecord.getVisits() == null ? 0L
				: sharedDocumentRecord.getVisits());
		return documentLink;
	}

}
