package login.repository;

import static login.domain.tables.Authorities.AUTHORITIES;
import static login.domain.tables.GroupAuthorities.GROUP_AUTHORITIES;
import static login.domain.tables.Groups.GROUPS;

import java.util.List;
import java.util.stream.Collectors;

import login.domain.tables.Authorities;
import login.domain.tables.GroupAuthorities;
import login.domain.tables.Groups;
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

	public void create(Authority authority) {
		AuthoritiesRecord auth = sql.newRecord(AUTHORITIES);
		auth.setUsername(authority.getUserName());
		auth.setAuthority(authority.getAuthority());
		auth.insert();
	}

	public List<Authority> getAll() {
		Result<AuthoritiesRecord> records = sql.fetch(AUTHORITIES);

		return records.stream().map(Authority::new)
				.collect(Collectors.toList());
	}

	public List<Authority> getAuthorityFromGroup(Long id) {
		GroupAuthorities ga = GROUP_AUTHORITIES.as("ga");
		Authorities a = AUTHORITIES.as("a");
		Groups g = GROUPS.as("g");

		List<Authority> authorities = sql.select(a.AUTHORITY).from(g).join(ga)
				.on(g.ID.eq(ga.GROUP_ID)).join(a)
				.on(a.AUTHORITY.eq(ga.AUTHORITY)).where(g.ID.eq(id))
				.fetchInto(Authority.class);

		/**
		 * 
		 * select *.a from group g join groupAuthorities ga on g.id =
		 * ga.group_id join authorities a on a.authority = ga.authority where
		 * g.id = id;
		 * 
		 */

		return authorities;
	}

	public void deleteByUserName(String userName) {
		sql.delete(AUTHORITIES).where(AUTHORITIES.USERNAME.eq(userName))
				.execute();
	}

}
