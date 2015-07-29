package accounts.repository;

import static accounts.domain.tables.Userconnection.USERCONNECTION;
import static accounts.domain.tables.Users.USERS;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.Userconnection;
import accounts.domain.tables.Users;

@Repository
public class AvatarRepository {

	@Autowired
	private DSLContext sql;

	public Optional<String> getAvatar(Long userId, String source) {
		Userconnection uc = USERCONNECTION.as("uc");
		Users u = USERS.as("u");

		// for now just return the first one found
		List<String> avatarUrls = sql
				.select(uc.IMAGEURL)
				.from(uc)
				.join(u)
				.on(uc.USERID.eq(u.USER_NAME))
				.where(u.USER_ID.eq(userId).and(
						uc.PROVIDERID.equalIgnoreCase(source)))
				.fetchInto(String.class);

		if (avatarUrls != null && !avatarUrls.isEmpty())
			return avatarUrls.stream().filter(s -> s != null && !s.isEmpty())
					.findFirst();
		return Optional.empty();
	}
}
