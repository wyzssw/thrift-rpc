package com.justdebugit.thrift.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.justdebugit.thrift.constants.Constants;

public class DefaultCuratorFactory implements CuratorFactory{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCuratorFactory.class);
	private String  connectString;//192.16.10.1:2181,192.16.10.2:2181,192.16.10.3:2181这种格式
	private Integer connectTimeout;
	private Integer sessionTimeout;
	
	public  DefaultCuratorFactory(String connString,Integer connectTimeout,Integer sessionTimeout) {
		this.connectString = connString;
		this.connectTimeout = connectTimeout;
		this.sessionTimeout = sessionTimeout;
	}
	
	
	public  DefaultCuratorFactory(String connString) {
		this(connString, Constants.DEFAULT_CONNECT_TIMEOUT, Constants.DEFAULT_SESSION_TIMEOUT);
	}
	

	@Override
	public CuratorFramework getCuratorClient() {
		Preconditions.checkNotNull(connectString);
		Builder builder = CuratorFrameworkFactory.builder()
				.connectString(connectString).sessionTimeoutMs(sessionTimeout)
				.retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 100))
				.connectionTimeoutMs(connectTimeout);
		CuratorFramework client  = builder.build();
		client.start();
		return client;
	}

	@Override
	public NodeCache getNodeCache(CuratorFramework client, String path) {
		Preconditions.checkArgument(client!=null && client.getState()==CuratorFrameworkState.STARTED,"client is not started");
		NodeCache nodeCache = new NodeCache(client, path);
		try {
			nodeCache.start(true);////同步初始化，将zk节点数据写入pathCache
		} catch (Exception e) {
			LOGGER.error("starting nodeCache error "+e.getMessage());
			throw new IllegalStateException(e.getMessage(), e);
		}
		return nodeCache;
	}

	@Override
	public PathChildrenCache getPathChildrenCache(CuratorFramework client,
			String path) {
		Preconditions.checkArgument(client!=null && client.getState()==CuratorFrameworkState.STARTED,"client is not started");
		PathChildrenCache childrenCache = new PathChildrenCache(client, path,true);
		try {
			childrenCache.start(StartMode.BUILD_INITIAL_CACHE);//同步初始化，将zk节点数据写入childrenCache
		} catch (Exception e) {
			LOGGER.error("starting nodeCache error "+e.getMessage());
			throw new IllegalStateException(e.getMessage(), e);
		}
		return childrenCache;
	}
	
	
	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setSessionTimeout(Integer sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

}
