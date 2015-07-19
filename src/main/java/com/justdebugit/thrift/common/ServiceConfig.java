package com.justdebugit.thrift.common;

import com.justdebugit.thrift.client.ThriftClientFactory;

public class ServiceConfig {
	
	
	private Class<?> interfaceClass;
	
	private Class<?> clientClass;
	
	private int minPoolSize;
	
	private int maxPoolSize;
	
	private ThriftClientFactory clientFactory;
	
	private String serviceName;
	
//	private ThriftStatefulPoolFactory<?> thriftStatefulPoolFactory;




	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public ServiceConfig setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
		return this;
	}

	public ThriftClientFactory getClientFactory() {
		return clientFactory;
	}

	public ServiceConfig setClientFactory(ThriftClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		return this;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public ServiceConfig setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
		return this;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public ServiceConfig setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		return this;
	}

//	public ThriftStatefulPoolFactory<?> getThriftStatefulPoolFactory() {
//		return thriftStatefulPoolFactory;
//	}
//
//	public RpcService setThriftStatefulPoolFactory(
//			ThriftStatefulPoolFactory<?> thriftStatefulPoolFactory) {
//		this.thriftStatefulPoolFactory = thriftStatefulPoolFactory;
//		return this;
//	}

	public Class<?> getClientClass() {
		return clientClass;
	}

	public void setClientClass(Class<?> clientClass) {
		this.clientClass = clientClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public ServiceConfig setServiceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}

	@Override
	public String toString() {
		return "ServiceConfig [interfaceClass="
				+ interfaceClass + ", minPoolSize=" + minPoolSize
				+ ", maxPoolSize=" + maxPoolSize + ", clientFactory="
				+ clientFactory + "]";
	}



	
	

}
