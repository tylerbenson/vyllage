package accounts.repository;

import static accounts.domain.tables.GroupMembers.GROUP_MEMBERS;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.GroupMembersRecord;
import accounts.model.GroupMember;

@Repository
public class GroupMemberRepository {

	@Autowired
	private DSLContext sql;

	public void create(GroupMember groupMember) {
		GroupMembersRecord newRecord = sql.newRecord(GROUP_MEMBERS);
		newRecord.setGroupId(groupMember.getGroup_id());
		newRecord.setUsername(groupMember.getUserName());
		newRecord.store();

	}

	public void deleteByUserName(String userName) {
		sql.delete(GROUP_MEMBERS).where(GROUP_MEMBERS.USERNAME.eq(userName))
				.execute();
	}
}
