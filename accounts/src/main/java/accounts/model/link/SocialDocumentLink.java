package accounts.model.link;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class SocialDocumentLink extends AbstractDocumentLink {

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime expirationDate;

	public SocialDocumentLink() {
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

}
