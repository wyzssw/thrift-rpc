package com.justdebugit.thrift.client;

import java.net.InetSocketAddress;

import org.apache.thrift.TException;

public interface ThriftClientFactory {
	
	<T> T createClient(String serviceName,Class<T> type,InetSocketAddress address);
	
	<T> void destroyClient(T client) throws TException;

}
