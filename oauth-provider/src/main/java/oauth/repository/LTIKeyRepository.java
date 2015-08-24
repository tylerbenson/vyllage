package oauth.repository;

import java.util.Optional;

import user.common.Organization;
import user.common.User;

public interface LTIKeyRepository {

	Optional<LTIKey> get(String consumerKey);

	LTIKey save(User user, Organization organization, String consumerKey,
			String secret, String externalOrganizationId);

	Organization getOrganizationByExternalId(String externalOrganizationId);

	Long getAuditUser(String externalOrganizationId);

}
