package accounts.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Returns the application context.
 * 
 * @author uh
 *
 */
public class ApplicationContextProvider implements ApplicationContextAware {
	private static ApplicationContext ctx = null;

	public ApplicationContextProvider(ApplicationContext ctx) {
		if (ApplicationContextProvider.ctx == null)
			ApplicationContextProvider.ctx = ctx;
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {

		if (ApplicationContextProvider.ctx == null)
			ApplicationContextProvider.ctx = ctx;
	}

}
