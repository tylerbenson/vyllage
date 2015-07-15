package accounts.model.link;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Object used to share a document link without creating a new user. Used to
 * share a link to a document to persons outside the system. The user id is to
 * get information about the user who created the link later.
 * 
 * @author uh
 *
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SocialDocumentLink extends AbstractDocumentLink {

	public SocialDocumentLink() {
	}

}
