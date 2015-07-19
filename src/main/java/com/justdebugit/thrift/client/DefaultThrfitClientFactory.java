package com.justdebugit.thrift.client;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.justdebugit.thrift.utils.ReflectUtils;

public class DefaultThrfitClientFactory implements ThriftClientFactory{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultThrfitClientFactory.class);
	
	private ConcurrentMap<Object, Closeable> clientMap = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createClient(String serviceName,Class<T> clazz, InetSocketAddress address) {
		Constructor<?> constructor =null;
	    try {
			constructor = 	ReflectUtils.findConstructor(clazz, TProtocol.class);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(),e);
		    throw new IllegalStateException("There is proper constructor "+e);
		}
		TFramedTransport transport = new TFramedTransport(new TSocket(address.getHostName(),
				address.getPort()));
		try {
			transport.open();
		} catch (TTransportException e) {
			LOGGER.error(e.getMessage(),e);
			transport.close();
			throw new IllegalStateException("Can not connect  to remote host "+e);
		}
		T client = null;
		try {
			client = (T)constructor.newInstance(new TMultiplexedProtocol(new TCompactProtocol(transport), serviceName));
			clientMap.putIfAbsent(client, transport);
		} catch (Exception e) {
			transport.close();
			throw new IllegalStateException("Can not create instance clazz "+e);
		}
		return client;
		
	}



	@Override
	public <T> void destroyClient(T client) throws TException {
		Closeable transport = clientMap.get(client);
		try {
			transport.close();
		} catch (IOException e) {
			throw new TException(e.getMessage(),e);
		}
	}

}
