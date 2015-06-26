package documents.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ElementNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4052672308416010337L;

	public ElementNotFoundException(String msg) {
		super(msg);
	}

}
