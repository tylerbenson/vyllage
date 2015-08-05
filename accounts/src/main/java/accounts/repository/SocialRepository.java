package accounts.repository;

import static accounts.domain.tables.Userconnection.USERCONNECTION;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import user.common.User;

@Repository
public class SocialRepository {

	@Autowired
	private DSLContext sql;

	/**
	 * Determines whether a user is connected to a given social network.
	 * 
	 * @param user
	 * @param network
	 * @return
	 */
	public boolean isConnected(User user, String network) {
		boolean exists = true;

		// User id is the username
		Result<Record> fetch = sql
				.select()
				.from(USERCONNECTION)
				.where(USERCONNECTION.USERID.eq(user.getUsername()).and(
						USERCONNECTION.PROVIDERID.equalIgnoreCase(network)))
				.fetch();

		if (fetch == null || fetch.isEmpty())
			return !exists;

		// probably twitter
		if (fetch.stream().allMatch(
				record -> record.getValue(USERCONNECTION.EXPIRETIME) == null))
			return exists;

		exists = fetch.stream().anyMatch(
				record -> record.getValue(USERCONNECTION.EXPIRETIME) != null
						&& System.currentTimeMillis() < record
								.getValue(USERCONNECTION.EXPIRETIME));

		return exists;
	}

}
