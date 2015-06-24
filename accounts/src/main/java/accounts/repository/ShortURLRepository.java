package accounts.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ShortURLRepository {

	@Autowired
	private DSLContext sql;

}
