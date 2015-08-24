package oauth.model.service;

import org.springframework.security.oauth.provider.nonce.InMemoryNonceServices;
import org.springframework.stereotype.Component;

@Component
public class LMSOAuthNonceServices extends InMemoryNonceServices {

	@Override
	public long getValidityWindowSeconds() {
		return 1200;
	}

}
