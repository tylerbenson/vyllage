package accounts.controller;

import javax.inject.Inject;

import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.stereotype.Controller;

@Controller
public class ConnectControllerWithRedirect extends ConnectController {

	@Inject
	public ConnectControllerWithRedirect(
			ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);

		this.setViewPath("/");
	}

	/**
	 * Returns the view name of a page to display for a provider when the user
	 * is connected to the provider. Typically this page would allow the user to
	 * disconnect from the provider. Defaults to
	 * "connect/{providerId}Connected". May be overridden to return a custom
	 * view name.
	 * 
	 * @param providerId
	 *            the ID of the provider to display the connection status for.
	 */
	@Override
	protected String connectedView(String providerId) {
		return "redirect:/account/setting";
	}

}
