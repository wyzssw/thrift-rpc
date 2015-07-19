package com.justdebugit.thrift.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;


public interface CuratorFactory {
	/**
	 * 新建一个curator client
	 * @return
	 */
	CuratorFramework getCuratorClient();	
	/**
	 * 创建一个nodeCache用于监听某阶段新建删除内容修改事件
	 * @param client
	 * @param path
	 * @return
	 */
	NodeCache        getNodeCache(CuratorFramework client,String path);
	/**
	 * 创建一个pathChildrenCache用于监听子节点增删改事件
	 * @param client
	 * @param path
	 * @return
	 */
	PathChildrenCache getPathChildrenCache(CuratorFramework client,String path);

}
