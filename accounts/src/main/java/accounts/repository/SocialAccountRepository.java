package accounts.repository;

import static accounts.domain.tables.SocialAccount.SOCIAL_ACCOUNT;

import java.util.Optional;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.SocialAccountRecord;

@Repository
public class SocialAccountRepository {

	private DSLContext sql;

	@Inject
	public SocialAccountRepository(DSLContext sql) {
		this.sql = sql;
	}

	public Optional<Long> getUserId(String providerId, String providerUserId) {
		SocialAccountRecord accountRecord = sql.fetchOne(
				SOCIAL_ACCOUNT,
				SOCIAL_ACCOUNT.PROVIDER.eq(providerId).and(
						SOCIAL_ACCOUNT.PROVIDER_USER_ID.eq(providerUserId)));

		if (accountRecord != null)
			return Optional.of(accountRecord.getUserId());
		return Optional.empty();
	}
}
