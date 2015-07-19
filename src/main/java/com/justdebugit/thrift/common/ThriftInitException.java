package com.justdebugit.thrift.common;

public class ThriftInitException extends RuntimeException {

	private static final long serialVersionUID = 4603401356618901407L;

	public ThriftInitException(String message, Throwable cause) {
		super(message, cause);
	}

	public ThriftInitException(String message) {
		super(message);
	}

	public ThriftInitException(Throwable cause) {
		super(cause);
	}


}
