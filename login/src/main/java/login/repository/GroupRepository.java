package login.repository;

import static login.domain.tables.Groups.GROUPS;

import java.util.List;

import login.model.Group;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepository {
	@Autowired
	private DSLContext sql;

	public List<Group> getAll() {

		return sql.select().from(GROUPS).fetch().into(Group.class);
	}
}
