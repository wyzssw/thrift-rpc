package com.justdebugit.thrift.provider;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.justdebugit.thrift.common.AbstractLifeCycle;
import com.justdebugit.thrift.constants.Constants;
import com.justdebugit.thrift.registry.CuratorFactory;
import com.justdebugit.thrift.registry.DefaultCuratorFactory;
import com.justdebugit.thrift.registry.Registry;
import com.justdebugit.thrift.registry.ZkCuratorRegistry;
import com.justdebugit.thrift.registry.ZkPathConstants;
import com.justdebugit.thrift.server.DefaultTServerFactory;
import com.justdebugit.thrift.server.TServerFactory;
import com.justdebugit.thrift.utils.NetUtils;
import com.justdebugit.thrift.utils.ZkRegistryUtils;

public class RpcProvider extends AbstractLifeCycle{
	private String host;
	private int port;  
	private TServer server ;
	private Registry registry;
	private Map<String, TProcessor> processorMap = Maps.newConcurrentMap();
	private Set<String> pathSet = Sets.newCopyOnWriteArraySet();
	
	
	public RpcProvider(ProviderBuilder builder){
		registry = makeRegistry(builder);
		port     = builder.getPort();
		host     = builder.getHost()==null?NetUtils.ANYHOST:builder.getHost(); 
		server   =  makeTserver(builder);
		processorMap.putAll(builder.getProcessorMap());
		
	}
	
	private TServer makeTserver(ProviderBuilder builder){
		server = builder.gettServer();
		if (server ==null) {
			TServerFactory tServerFactory = null;
			if ((tServerFactory=builder.gettServerFactory())!=null) {
				server = tServerFactory.createTServer();
			}else {
				Preconditions.checkArgument(!builder.getProcessorMap().isEmpty(), "processor map can not be null ");
				Preconditions.checkArgument(builder.getPort()!=0, "port must be greater than zero");
				InetSocketAddress address = null;
				if (builder.getHost()!=null) {
					 address = new InetSocketAddress(builder.getHost(), port);
				}else {
					 address = new InetSocketAddress(port);
				}
				
				TServerFactory _tServerFactory  = new DefaultTServerFactory(builder.getProcessorMap(), address);
				server = _tServerFactory.createTServer();
			}
		}
		return server;
		
	}

	private Registry makeRegistry(ProviderBuilder builder) {
		registry = builder.getRegistry();
		if (registry==null) {
			CuratorFactory curatorFactory = new DefaultCuratorFactory(
					builder.getConnectStringForzk(),
					builder.getConectTimeoutForZk(),
					builder.getSessionTimeoutForZk());
			registry = new ZkCuratorRegistry(curatorFactory);
		}
		return registry;
	}
	

	public void doStart() {
		for (Map.Entry<String, TProcessor> entry : processorMap.entrySet()) {
			StringBuilder pathBuilder = new StringBuilder("/"+entry.getKey()+ZkPathConstants.PROVIDER_SUFFIX_PATH);
			pathBuilder.append("/"+host+":"+port);
			String path = pathBuilder.toString();
			pathSet.add(path);
			ZkRegistryUtils.registerWithGuaranteed(registry, path, String.valueOf(System.currentTimeMillis()).getBytes());
		}
		server.serve();
	}

	public void doStop() {
		server.stop();
		for (String path : pathSet) {
			 registry.unregister(path);
			 pathSet.remove(path);
		}
	}
	
	public static ProviderBuilder builder(){
		return new ProviderBuilder();
	}

	public static class ProviderBuilder{
		private String host;
		private int port;
		private String connectStringForzk ;
		private int    sessionTimeoutForZk = Constants.DEFAULT_SESSION_TIMEOUT;
		private int    conectTimeoutForZk  = Constants.DEFAULT_CONNECT_TIMEOUT;
		
		private Registry registry;
		private TServerFactory tServerFactory;
		private TServer tServer;
		
		private Map<String, TProcessor> processorMap  = Maps.newConcurrentMap();
		
		public  RpcProvider build(){
			return new RpcProvider(this);
		}
		
		public ProviderBuilder processorMap(Map<String, TProcessor> map){
			processorMap.putAll(map);
			return this;
		}
		
		public ProviderBuilder connectStringForzk(String connectString){
			this.connectStringForzk = connectString;
			return this;
		}
		
		public ProviderBuilder sessionTimeoutForZk(int sessionTimeoutForZk){
			this.sessionTimeoutForZk = sessionTimeoutForZk;
			return this;
		}
		
		public ProviderBuilder connectTimeoutForZk(int connectTimeoutForZk){
			this.conectTimeoutForZk = connectTimeoutForZk;
			return this;
		}
		
		public ProviderBuilder serverPort(int port){
			this.port = port;
			return this;
		}
		
		public ProviderBuilder serverHost(String hostname){
			this.host= hostname;
			return this;
		}
		
		public ProviderBuilder tServerFactory(TServerFactory tServerFactory){
			this.tServerFactory = tServerFactory;
			return this;
		}
		
		public ProviderBuilder tServer(TServer tServer){
			this.tServer = tServer;
			return this;
		}
		
		public ProviderBuilder registry(Registry registry){
			this.registry = registry;
			return this;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public String getConnectStringForzk() {
			return connectStringForzk;
		}

		public int getSessionTimeoutForZk() {
			return sessionTimeoutForZk;
		}

		public int getConectTimeoutForZk() {
			return conectTimeoutForZk;
		}

		public Registry getRegistry() {
			return registry;
		}

		public TServerFactory gettServerFactory() {
			return tServerFactory;
		}

		public TServer gettServer() {
			return tServer;
		}

		public Map<String, TProcessor> getProcessorMap() {
			return processorMap;
		}
	}


}
