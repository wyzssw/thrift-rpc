package com.justdebugit.thrift.consumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.justdebugit.thrift.client.ThriftClientFactory;
import com.justdebugit.thrift.client.ThriftStatefulPoolFactory;
import com.justdebugit.thrift.common.AbstractLifeCycle;
import com.justdebugit.thrift.common.ServiceConfig;
import com.justdebugit.thrift.consumer.RpcConsumerFactory.Builder;
import com.justdebugit.thrift.pool.MultiPool;
import com.justdebugit.thrift.pool.StatefulPoolFactory;
import com.justdebugit.thrift.pool.StatefulPoolMapManager;
import com.justdebugit.thrift.proxy.PooledProxyFactory;
import com.justdebugit.thrift.registry.CuratorFactory;
import com.justdebugit.thrift.registry.DefaultCuratorFactory;
import com.justdebugit.thrift.registry.Registry;
import com.justdebugit.thrift.registry.ZkCuratorRegistry;

public class RpcConsumer extends AbstractLifeCycle{
	
	private  List<ServiceConfig> services;
	
	private  String    connectStringForZk;
	
	private  int       sessionTimeOutForZk;
	
	private  int       connectTimeOutForZk;
	
	private  final      ConcurrentMap<String, Object> proxyMap = Maps.newConcurrentMap();
	private  final      Map<ServiceConfig, MultiPool<?>>   poolMap = Maps.newHashMap();
	
	
	private  final      Registry registry;
	private  final      ThriftClientFactory thriftClientFactory;

	public RpcConsumer(Builder builder) {
		connectStringForZk  = builder.getConnectString();
		sessionTimeOutForZk = builder.getSessionTimeout();
		connectTimeOutForZk = builder.getConnectTimeout();
		CuratorFactory curatorFactory = new DefaultCuratorFactory(connectStringForZk,connectTimeOutForZk,sessionTimeOutForZk);
		this.registry = new ZkCuratorRegistry(curatorFactory);
		this.thriftClientFactory = builder.getDefaultThriftClientFactory();
		services = builder.getRpcServices();
		for (ServiceConfig rpcService : services) {
			 Preconditions.checkNotNull(rpcService.getInterfaceClass());
			 if (rpcService.getClientFactory()==null) {
			     rpcService.setClientFactory(thriftClientFactory);	
			 }
			 String serviceName =  rpcService.getServiceName()==null?rpcService.getInterfaceClass().getName():rpcService.getServiceName();
			 Preconditions.checkNotNull(serviceName, "Servicename must not be null");
			 rpcService.setServiceName(serviceName);
			 proxyMap.put(serviceName, buildServiceProxy(rpcService));
		}
	}
	
	
	public void addService(ServiceConfig rpcService){
		 Preconditions.checkNotNull(rpcService.getInterfaceClass());
		 if (rpcService.getClientFactory()==null) {
		     rpcService.setClientFactory(thriftClientFactory);	
		 }
		 proxyMap.put(rpcService.getServiceName(), buildServiceProxy(rpcService));
		
	}
	
	
	@SuppressWarnings("unchecked")
	private <T> T buildServiceProxy(ServiceConfig rpcService){
		MultiPool<T> pool = buildMultiPool(rpcService);
		poolMap.put(rpcService, pool);
		return PooledProxyFactory.getProxy((Class<T>)rpcService.getInterfaceClass(),(Class<? extends T>)rpcService.getClientClass(),pool);
		
	}
	
	private <T> MultiPool<T> buildMultiPool(ServiceConfig rpcService) {
		StatefulPoolFactory<T> statefulPoolFactory = new ThriftStatefulPoolFactory<T>(rpcService);
		StatefulPoolMapManager<T> statefulPoolMapManager = new StatefulPoolMapManager<T>(rpcService.getServiceName(), registry, statefulPoolFactory);
		MultiPool<T> pool = new MultiPool<T>(statefulPoolMapManager);
		return pool;
	}



	public List<ServiceConfig> getServices() {
		return services;
	}

	public void setServices(List<ServiceConfig> services) {
		this.services = services;
	}

	public String getConnectStringForZk() {
		return connectStringForZk;
	}

	public void setConnectStringForZk(String connectStringForZk) {
		this.connectStringForZk = connectStringForZk;
	}

	public int getSessionTimeOutForZk() {
		return sessionTimeOutForZk;
	}

	public void setSessionTimeOutForZk(int sessionTimeOutForZk) {
		this.sessionTimeOutForZk = sessionTimeOutForZk;
	}

	public int getConnectTimeOutForZk() {
		return connectTimeOutForZk;
	}

	public void setConnectTimeOutForZk(int connectTimeOutForZk) {
		this.connectTimeOutForZk = connectTimeOutForZk;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T>  T getService(String serviceName){
		return (T)proxyMap.get(serviceName);
	}


	@Override
	protected void doStart() {
		for (Map.Entry<ServiceConfig,MultiPool<?>> entry : poolMap.entrySet()) {
			 MultiPool<?> multiPool = entry.getValue();
			 multiPool.start();
		}
		
	}


	@Override
	protected void doStop() {
		for (Map.Entry<ServiceConfig,MultiPool<?>> entry : poolMap.entrySet()) {
			 MultiPool<?> multiPool = entry.getValue();
			 multiPool.stop();
		}
		
	}



}
