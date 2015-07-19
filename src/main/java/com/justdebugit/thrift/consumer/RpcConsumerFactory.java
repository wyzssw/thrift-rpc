package com.justdebugit.thrift.consumer;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.justdebugit.thrift.client.DefaultThrfitClientFactory;
import com.justdebugit.thrift.client.ThriftClientFactory;
import com.justdebugit.thrift.common.ServiceConfig;
import com.justdebugit.thrift.constants.Constants;



public class RpcConsumerFactory {
	
	public static Builder builder(){
		return new Builder() {
		};
	}
	
	public static RpcConsumer newRpcConsumer(String zkConnectString,List<ServiceConfig> list) {
		return builder().connectStringForZk(zkConnectString).addAllRpcServices(list)
				.connectTimeoutForZk(6000)
				.sessionTimeoutForZk(2000).build();
	}

	public static RpcConsumer newRpcConsumer(String connectString, int sessionTimeout,
			int connectTimeout,List<ServiceConfig> list) {
		return builder().connectStringForZk(connectString).addAllRpcServices(list)
				.sessionTimeoutForZk(sessionTimeout).connectTimeoutForZk(connectTimeout)
				.build();
	}
	
	public static class Builder{
		private String connectString;
		private int    sessionTimeout = Constants.DEFAULT_SESSION_TIMEOUT;
		private int    connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT;
		private ThriftClientFactory defaultThriftClientFactory = new DefaultThrfitClientFactory();
		private List<ServiceConfig> list = Lists.newArrayList();
		
		
		
		
		public static Builder builder(){
			return new Builder();
		}
		
		public  RpcConsumer build(){
			return new RpcConsumer(this);
		}
		
		public Builder addAllRpcServices(Collection<? extends ServiceConfig> rpcServices){
			list.addAll(rpcServices);
			return this;
		}
		
		public Builder addRpcService(ServiceConfig rpcService){
			list.add(rpcService);
			return this;
		}
		
		public Builder connectStringForZk(String connectString){
			this.connectString = connectString;
			return this;
		}
		
		public Builder sessionTimeoutForZk(int sessionTimeout){
			this.sessionTimeout = sessionTimeout;
			return this;
		}
		
		public Builder connectTimeoutForZk(int connectTimeout){
			this.connectTimeout = connectTimeout;
			return this;
		}

		public String getConnectString() {
			return connectString;
		}

		public int getSessionTimeout() {
			return sessionTimeout;
		}

		public int getConnectTimeout() {
			return connectTimeout;
		}
		
		public ThriftClientFactory getDefaultThriftClientFactory() {
			return defaultThriftClientFactory;
		}
		
		public List<ServiceConfig> getRpcServices(){
			return list;
		}

		public Builder defaultThriftClientFactory(
				ThriftClientFactory defaultThriftClientFactory) {
			this.defaultThriftClientFactory = defaultThriftClientFactory;
			return this;
		}
	}

}
