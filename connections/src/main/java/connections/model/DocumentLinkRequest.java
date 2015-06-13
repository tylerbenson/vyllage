package connections.model;

import java.time.LocalDateTime;

import lombok.ToString;
import util.dateSerialization.LocalDateTimeDeserializer;
import util.dateSerialization.LocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Request object to generate a link for a particular document for another user.
 * 
 * @author uh
 *
 */
@ToString
public class DocumentLinkRequest {

	private String firstName;

	private String lastName;

	private String email;

	private Long documentId;

	private String documentType;

	private boolean sendRegistrationMail = false;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime expirationDate;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public boolean sendRegistrationMail() {
		return sendRegistrationMail;
	}

	public void setSendRegistrationMail(boolean sendRegistrationMail) {
		this.sendRegistrationMail = sendRegistrationMail;
	}

}
