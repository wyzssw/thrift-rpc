package com.justdebugit.thrift.client;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.justdebugit.fastpool.ObjectFactory;
import com.justdebugit.fastpool.Pool;
import com.justdebugit.fastpool.ScalableFastPool;
import com.justdebugit.thrift.common.ServiceConfig;
import com.justdebugit.thrift.pool.DelegatableStatefulPool;
import com.justdebugit.thrift.pool.State;
import com.justdebugit.thrift.pool.StatefulPool;
import com.justdebugit.thrift.pool.StatefulPoolFactory;

public class ThriftStatefulPoolFactory<T> implements StatefulPoolFactory<T> {
	private Class<T> type;
	private ThriftClientFactory thriftFactory;
	private ServiceConfig rpcService;
	private String  serviceName;
	
	
	public ThriftStatefulPoolFactory() {}
	
	@SuppressWarnings("unchecked")
	public ThriftStatefulPoolFactory(ServiceConfig rpcService) {
		Preconditions.checkNotNull(rpcService.getServiceName());
		this.rpcService = rpcService;
		this.serviceName = rpcService.getServiceName();
		this.type = (Class<T>)rpcService.getClientClass();
		this.thriftFactory = rpcService.getClientFactory();
	}
	
	public ThriftStatefulPoolFactory(Class<T> type,ThriftClientFactory thriftFactory) {
		this.type = type;
		this.thriftFactory = thriftFactory;
	}

	@Override
	public StatefulPool<T> getPool(String host_port) {
		Preconditions.checkNotNull(type);
		Iterable<String> iterable = Splitter.on(":").split(host_port);
	    List<String>  list = Lists.newArrayList(iterable);
	    final String host = StringUtils.substringAfterLast(list.get(0), "/");
		final int    port = Integer.valueOf(list.get(1));
	    
		final InetSocketAddress remoteAddress =  new InetSocketAddress(host, port);
		ObjectFactory<T> objectFactory = new ObjectFactory<T>() {

			@Override
			public T makeObject() {
				return thriftFactory.createClient(serviceName,type, remoteAddress);
			}

			@Override
			public void destroyObject(T client) throws Exception {
				thriftFactory.destroyClient(client);
			}
		};
		Pool<T> scalableFastPool = null;
		if (rpcService!=null && rpcService.getMaxPoolSize()>0 && rpcService.getMinPoolSize() >0) {
			scalableFastPool = new ScalableFastPool<T>(rpcService.getMinPoolSize(),rpcService.getMaxPoolSize(),objectFactory);
		}else {
			scalableFastPool = new ScalableFastPool<T>(objectFactory);
		}
		StatefulPool<T> statefulPool = new DelegatableStatefulPool<T>(scalableFastPool);
		statefulPool.state().set(State.INITIALIZED);
		statefulPool.getRefCnt().set(0);
		return statefulPool;
	}



	@Override
	public Class<T> getType() {
		return type;
	}


	@Override
	public void setType(Class<T> type) {
		this.type = type;
		
	}
	
	

}
