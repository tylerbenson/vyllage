package email;

import org.thymeleaf.context.Context;

public class EmailContext extends Context {

	public final String templateName;

	public EmailContext(String templateName) {
		super();
		this.templateName = templateName;
	}

}
