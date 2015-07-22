package accounts.model.link;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Object used to share a link for a document. Creating a user in the system for
 * the intended recipient. The userId is the user who has access to the
 * document.
 * 
 * @author uh
 *
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmailDocumentLink extends AbstractDocumentLink {

}
