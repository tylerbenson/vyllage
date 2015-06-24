package accounts.model.link;

import java.time.LocalDateTime;

import lombok.ToString;
import accounts.domain.tables.records.SharedDocumentRecord;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Object used to share a document link without creating a new user. Used to
 * share a link to a document to persons outside the system. The user id is to
 * get information about the user who created the link later.
 * 
 * @author uh
 *
 */
@ToString
public class SocialDocumentLink {

	private Long userId;
	private Long documentId;
	private String documentType;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime expirationDate;
	private String documentURL;
	private LinkType linkType;
	private Long visits;

	public SocialDocumentLink() {
	}

	public SocialDocumentLink(SharedDocumentRecord record) {
		this.documentId = record.getDocumentId();
		this.documentType = record.getDocumentType();
		this.linkType = LinkType.valueOf(record.getLinkType());
		this.documentURL = record.getShortUrl();
		this.userId = record.getUserId();
		this.expirationDate = record.getExpirationDate().toLocalDateTime();
		this.visits = record.getVisits();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getDocumentURL() {
		return this.documentURL;
	}

	public void setDocumentURL(String documentURL) {
		this.documentURL = documentURL;
	}

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

	public Long getVisits() {
		return visits;
	}

	public void setVisits(Long visits) {
		this.visits = visits;
	}

}
