package connections.email;

/**
 * Html email body, using the specified html template and key value context
 * generates the html email body.
 * 
 * @author uh
 *
 */
public class EmailHTMLBody extends EmailBody {

	public final EmailContext ctx;

	/**
	 * 
	 * @param txt
	 *            text body in case client doesn't support html.
	 * @param ctx
	 *            key value map with the template that's going to be used to
	 *            generate the html body.
	 */
	public EmailHTMLBody(String txt, EmailContext ctx) {
		super(txt);

		this.ctx = ctx;
	}

}
