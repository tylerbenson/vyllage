package connections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;

//For later.
//@Component("connections.EnvironmentBeanResolver")
public class EnvironmentBeanResolver implements BeanResolver {

	@Autowired
	private Environment environment;

	@Override
	public Object resolve(EvaluationContext context, String beanName)
			throws AccessException {

		if (beanName.equalsIgnoreCase("environment")) {
			return environment;
		}

		return null;
	}

}
