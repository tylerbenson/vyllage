package accounts.controller;

import javax.inject.Inject;

import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;

public class ConnectControllerWithRedirect extends ConnectController {

	@Inject
	public ConnectControllerWithRedirect(
			ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);

		this.setViewPath("/");
	}

	@Override
	protected String connectedView(String providerId) {
		return "redirect:/account/setting";
	}

}
