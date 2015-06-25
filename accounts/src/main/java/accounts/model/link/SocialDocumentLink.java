package accounts.model.link;

import java.time.LocalDateTime;

import lombok.ToString;

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
	private String linkKey;
	private LinkType linkType;
	private Long visits;

	public SocialDocumentLink() {
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

	public String getLinkKey() {
		return linkKey;
	}

	public void setLinkKey(String linkKey) {
		this.linkKey = linkKey;
	}

}
