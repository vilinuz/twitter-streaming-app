package org.twitter.streaming.app.exception;

public class TwitterConfigurationException extends RuntimeException {

	public TwitterConfigurationException() {
		super();
	}

	public TwitterConfigurationException(final String message) {
		super(message);
	}

	public TwitterConfigurationException(final String message, final Throwable t) {
		super(message, t);
	}

	public TwitterConfigurationException(final Throwable t) {
		super(t);
	}
}
