package accounts.repository;

import static accounts.domain.tables.OrganizationMembers.ORGANIZATION_MEMBERS;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.OrganizationMembersRecord;
import accounts.model.OrganizationMember;

@Repository
public class OrganizationMemberRepository {

	@Autowired
	private DSLContext sql;

	public void create(OrganizationMember organizationMember) {
		OrganizationMembersRecord newRecord = sql
				.newRecord(ORGANIZATION_MEMBERS);
		newRecord.setOrganizationId(organizationMember.getOrganizationId());
		newRecord.setUserId(organizationMember.getUserId());
		newRecord.store();

	}

	public void deleteByUserId(Long userId) {
		sql.delete(ORGANIZATION_MEMBERS)
				.where(ORGANIZATION_MEMBERS.USER_ID.eq(userId)).execute();
	}

	public List<OrganizationMember> getByUserId(Long userId) {
		return sql.fetch(ORGANIZATION_MEMBERS,
				ORGANIZATION_MEMBERS.USER_ID.eq(userId)).into(
				OrganizationMember.class);
	}
}
