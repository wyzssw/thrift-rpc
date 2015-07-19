package com.justdebugit.thrift.pool;

import com.justdebugit.fastpool.ObjectFactory;

/**
 * abstract objectFactory for connection
 * @author justdebugit@gmail.com
 *
 * @param <T>
 */
public abstract class  AbstractObjectFactory<T> implements ObjectFactory<T>{
	private String host;
	private int    port;
	
	public AbstractObjectFactory(String host,int port) {
		 this.host = host;
		 this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	
}
