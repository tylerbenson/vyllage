package accounts.repository;

import static accounts.domain.tables.OrganizationMembers.ORGANIZATION_MEMBERS;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.OrganizationMembersRecord;
import accounts.model.GroupMember;

@Repository
public class GroupMemberRepository {

	@Autowired
	private DSLContext sql;

	public void create(GroupMember groupMember) {
		OrganizationMembersRecord newRecord = sql
				.newRecord(ORGANIZATION_MEMBERS);
		newRecord.setOrganizationId(groupMember.getGroup_id());
		newRecord.setUserName(groupMember.getUserName());
		newRecord.store();

	}

	public void deleteByUserName(String userName) {
		sql.delete(ORGANIZATION_MEMBERS)
				.where(ORGANIZATION_MEMBERS.USER_NAME.eq(userName)).execute();
	}
}
