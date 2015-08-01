package accounts.repository;

import static accounts.domain.tables.Organizations.ORGANIZATIONS;
import accounts.domain.tables.records.OrganizationsRecord;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import com.newrelic.api.agent.NewRelic;

import user.common.Organization;

@Repository
public class OrganizationRepository {

	@Autowired
	private DSLContext sql;

	@Autowired
	private DataSourceTransactionManager txManager;

	public Organization get(Long id) {
		return sql.fetchOne(ORGANIZATIONS, ORGANIZATIONS.ORGANIZATION_ID.eq(id)).into(Organization.class);
	}

	public Organization getByName(String name) {
		try {
			return sql.fetchOne(ORGANIZATIONS, ORGANIZATIONS.ORGANIZATION_NAME.eq(name)).into(Organization.class);
		} catch (NullPointerException ex) {
		}
		return null;
	}

	public List<Organization> getAll() {
		return sql.select().from(ORGANIZATIONS).fetch().into(Organization.class);
	}

	public List<Organization> getAll(List<Long> organizationIds) {
		return sql.select().from(ORGANIZATIONS).where(ORGANIZATIONS.ORGANIZATION_ID.in(organizationIds)).fetch()
				.into(Organization.class);
	}

	public Organization addOrganization(Organization organization) {

		TransactionStatus transaction = txManager.getTransaction(new DefaultTransactionDefinition());
		Object savepoint = transaction.createSavepoint();
		Organization organizationData = null;
		try {
			OrganizationsRecord newRecord = sql.newRecord(ORGANIZATIONS);
			newRecord.setOrganizationName(organization.getOrganizationName());
			newRecord.store();
			Assert.notNull(newRecord.getOrganizationId());
			organizationData = new Organization(newRecord.getOrganizationId(), newRecord.getOrganizationName());
		} catch (Exception e) {
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			txManager.commit(transaction);
		}
		return organizationData;
	}
}
