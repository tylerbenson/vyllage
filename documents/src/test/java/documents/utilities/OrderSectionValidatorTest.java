package documents.utilities;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class OrderSectionValidatorTest {

	@Test(expected = IllegalArgumentException.class)
	public void checkNullOrEmptyParametersDocumentNull() {
		OrderSectionValidator validator = new OrderSectionValidator();

		validator.checkNullOrEmptyParameters(null, Arrays.asList(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkNullOrEmptyParametersIdsNull() {
		OrderSectionValidator validator = new OrderSectionValidator();

		validator.checkNullOrEmptyParameters(1L, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkNullOrEmptyParametersIdsEmpty() {
		OrderSectionValidator validator = new OrderSectionValidator();

		validator.checkNullOrEmptyParameters(1L, new ArrayList<>());
	}

	@Test
	public void checkNullOrEmptyParametersOk() {
		OrderSectionValidator validator = new OrderSectionValidator();

		validator.checkNullOrEmptyParameters(1L, Arrays.asList(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void duplicateIds() {
		OrderSectionValidator validator = new OrderSectionValidator();

		validator.checkDuplicateSectionIds(Arrays.asList(1L, 1L, 2L));
	}

	@Test
	public void duplicateIdsOk() {
		OrderSectionValidator validator = new OrderSectionValidator();

		validator.checkDuplicateSectionIds(Arrays.asList(1L, 2L));
	}

}
