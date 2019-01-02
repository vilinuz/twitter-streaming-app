package org.twitter.streaming.app.exception;

public class TwitterAuthenticationException extends RuntimeException {

	public TwitterAuthenticationException() {
		super();
	}
	
	public TwitterAuthenticationException(final String message) {
		super(message);
	}
	
	public TwitterAuthenticationException(final String message, final Throwable t) {
		super(message, t);
	}
	
	public TwitterAuthenticationException(final Throwable t) {
		super(t);
	}
}
