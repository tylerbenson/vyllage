package accounts.service;

import javax.inject.Inject;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Service;

@Service
public class SocialService {

	private ConnectionRepository connectionRepository;

	@Inject
	public SocialService(ConnectionRepository connectionRepository) {
		this.connectionRepository = connectionRepository;
	}

	// public boolean connectionExists(ConnectionData createData) {
	// return connectionRepository.;
	// }
}
