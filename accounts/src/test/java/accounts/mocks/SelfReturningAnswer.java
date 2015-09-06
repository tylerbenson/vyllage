package accounts.mocks;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * From https://stackoverflow.com/questions/8501920/how-to-mock-a-builder-with
 * -mockito
 */
public class SelfReturningAnswer implements Answer<Object> {

	@Override
	public Object answer(InvocationOnMock invocation) throws Throwable {
		Object mock = invocation.getMock();
		if (invocation.getMethod().getReturnType().isInstance(mock)) {
			return mock;
		} else {
			return Mockito.RETURNS_DEFAULTS.answer(invocation);
		}
	}

}
