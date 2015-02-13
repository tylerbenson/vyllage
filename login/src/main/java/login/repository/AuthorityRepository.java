package login.repository;

import static login.domain.tables.Authorities.AUTHORITIES;

import java.util.List;
import java.util.stream.Collectors;

import login.domain.tables.records.AuthoritiesRecord;
import login.model.Authority;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorityRepository {

	@Autowired
	private DSLContext sql;

	public List<Authority> getByUserName(String userName) {
		Result<AuthoritiesRecord> records = sql.fetch(AUTHORITIES,
				AUTHORITIES.USERNAME.eq(userName));

		return records.stream().map(Authority::new)
				.collect(Collectors.toList());
	}

	public List<Authority> getAll() {
		Result<AuthoritiesRecord> records = sql.fetch(AUTHORITIES);

		return records.stream().map(Authority::new)
				.collect(Collectors.toList());
	}

}
