package com.justdebugit.thrift.pool;

public final class IllegalPoolStateException extends RuntimeException {
	
	private static final long serialVersionUID = 1571857199149792306L;

	public IllegalPoolStateException() {
		super();
	}


	public IllegalPoolStateException(String message) {
		super(message);
	}


}
