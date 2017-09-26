package io.elken.fakebook.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_GATEWAY)
public class ServiceException extends RuntimeException {

	public ServiceException() {
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

}
