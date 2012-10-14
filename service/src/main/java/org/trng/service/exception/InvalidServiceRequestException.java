package org.trng.service.exception;

public class InvalidServiceRequestException extends Exception {
	private static final long serialVersionUID = 6682253943396049547L;

	public InvalidServiceRequestException() {
		super();
	}

	public InvalidServiceRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidServiceRequestException(String message) {
		super(message);
	}

	public InvalidServiceRequestException(Throwable cause) {
		super(cause);
	}

}
