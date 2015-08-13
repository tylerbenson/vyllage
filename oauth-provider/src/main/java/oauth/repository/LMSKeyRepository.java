package oauth.repository;

import java.util.Optional;

import user.common.Organization;
import user.common.User;

public interface LMSKeyRepository {

	Optional<LMSKey> get(String consumerKey);

	LMSKey save(User user, Organization organization, String consumerKey,
			String secret);

}
