package accounts.repository;

import static accounts.domain.tables.Emails.EMAILS;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.model.Email;

@Repository
public class EmailRepository {

	@Autowired
	private DSLContext sql;

	public List<Email> getByUserId(Long userId) {
		return sql.fetch(EMAILS, EMAILS.USER_ID.eq(userId)).into(Email.class);
	}

}
