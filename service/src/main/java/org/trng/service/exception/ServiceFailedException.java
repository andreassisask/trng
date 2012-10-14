package org.trng.service.exception;

public class ServiceFailedException extends Exception {
	private static final long serialVersionUID = 6682253943396049547L;

	public ServiceFailedException() {
		super();
	}

	public ServiceFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceFailedException(String message) {
		super(message);
	}

	public ServiceFailedException(Throwable cause) {
		super(cause);
	}

}
