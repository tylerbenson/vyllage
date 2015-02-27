package login.repository;

import static login.domain.tables.GroupAuthorities.GROUP_AUTHORITIES;
import static login.domain.tables.Groups.GROUPS;

import java.util.List;

import login.domain.tables.GroupAuthorities;
import login.domain.tables.Groups;
import login.model.Group;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepository {
	@Autowired
	private DSLContext sql;

	public Group get(Long id) {
		return sql.fetchOne(GROUPS, GROUPS.ID.eq(id)).into(Group.class);
	}

	public List<Group> getAll() {
		return sql.select().from(GROUPS).fetch().into(Group.class);
	}

	public List<Group> getGroupFromAuthority(String authority) {
		Groups g = GROUPS.as("g");
		GroupAuthorities ga = GROUP_AUTHORITIES.as("ga");

		return sql.select(g.fields()).from(ga).join(g).on(ga.GROUP_ID.eq(g.ID))
				.where(ga.AUTHORITY.eq(authority)).fetchInto(Group.class);
		/**
		 * select *.g from group_authorities ga join groups g on ga.group_id =
		 * g.id;
		 */
	}
}
