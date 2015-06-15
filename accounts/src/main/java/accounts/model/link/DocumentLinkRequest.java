package accounts.model.link;

import java.time.LocalDateTime;

import lombok.ToString;
import util.dateSerialization.LocalDateTimeDeserializerMs;
import util.dateSerialization.LocalDateTimeSerializerMs;

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

	private boolean sendRegistrationMail;

	@JsonSerialize(using = LocalDateTimeSerializerMs.class)
	@JsonDeserialize(using = LocalDateTimeDeserializerMs.class)
	private LocalDateTime expirationDate;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
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
