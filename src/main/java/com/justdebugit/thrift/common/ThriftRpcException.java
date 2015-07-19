package com.justdebugit.thrift.common;

public class ThriftRpcException extends RuntimeException {

	private static final long serialVersionUID = 4603401356618901407L;

	public ThriftRpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public ThriftRpcException(String message) {
		super(message);
	}

	public ThriftRpcException(Throwable cause) {
		super(cause);
	}


}
