package connections.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;

/**
 * Html email body, using the specified html template and key value context
 * generates the html email body.
 * 
 * @author uh
 *
 */
public class EmailHTMLBody extends EmailBody {

	@Autowired
	private TemplateEngine templateEngine;

	public final String html;

	/**
	 * 
	 * @param txt
	 *            text body in case client does't support html.
	 * @param ctx
	 *            key value map with the template that's going to be used to
	 *            generate the html body.
	 */
	public EmailHTMLBody(String txt, EmailContext ctx) {
		super(txt);

		this.html = templateEngine.process(ctx.templateName, ctx);
	}

}
