package accounts.service.contactSuggestion;

import java.util.Map;
import java.util.Optional;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import user.common.User;

public class CurrentStudentContactSelector extends AbstractContactSelector {

	@Override
	protected Optional<SelectConditionStep<Record>> getSuggestions(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void applyFilters(
			SelectConditionStep<Record> selectConditionStep,
			Map<String, String> filters) {
		// TODO Auto-generated method stub

	}

}
