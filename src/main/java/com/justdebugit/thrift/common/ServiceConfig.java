package com.justdebugit.thrift.common;

import com.justdebugit.thrift.client.ThriftClientFactory;

public class ServiceConfig {
	
	private Class<?> serviceClass;
	
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

	public Class<?> getClientClass() {
		return clientClass;
	}

	public ServiceConfig setClientClass(Class<?> clientClass) {
		this.clientClass = clientClass;
		return this;
	}

	public String getServiceName() {
		return serviceName;
	}

	public ServiceConfig setServiceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}

	public Class<?> getServiceClass() {
		return serviceClass;
	}

	public ServiceConfig setServiceClass(Class<?> serviceClass) {
		this.serviceClass = serviceClass;
		if (interfaceClass==null) {
			try {
				Class<?> clazz = Class.forName(serviceClass.getName()+"$Iface");
				interfaceClass = clazz;
				Class<?> clientClazz = Class.forName(serviceClass.getName()+"$Client");
				clientClass = clientClazz;
			} catch (ClassNotFoundException e) {
				throw new ThriftInitException(" can not find class of  "+serviceClass+"$Iface.class or "+serviceClass+ "$Client; "  +e.getMessage(), e);
			}
		}
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
