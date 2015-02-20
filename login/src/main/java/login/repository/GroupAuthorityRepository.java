package login.repository;

import static login.domain.tables.GroupAuthorities.GROUP_AUTHORITIES;
import login.model.GroupAuthority;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GroupAuthorityRepository {

	@Autowired
	private DSLContext sql;

	public GroupAuthority getGroupAuthorityFromGroup(long id) {

		return sql.fetchOne(GROUP_AUTHORITIES,
				GROUP_AUTHORITIES.GROUP_ID.eq(id)).into(GroupAuthority.class);
	}

}
