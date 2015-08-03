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

	public Organization get(Long id) {
		return sql.fetchOne(ORGANIZATIONS, ORGANIZATIONS.ORGANIZATION_ID.eq(id)).into(Organization.class);
	}

	public Organization getByName(String name) {
		return sql.fetchOne(ORGANIZATIONS, ORGANIZATIONS.ORGANIZATION_NAME.eq(name)).into(Organization.class);
	}

	public List<Organization> getAll() {
		return sql.select().from(ORGANIZATIONS).fetch().into(Organization.class);
	}

	public List<Organization> getAll(List<Long> organizationIds) {
		return sql.select().from(ORGANIZATIONS).where(ORGANIZATIONS.ORGANIZATION_ID.in(organizationIds)).fetch()
				.into(Organization.class);
	}

}
