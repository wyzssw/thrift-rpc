package com.justdebugit.thrift.registry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.justdebugit.thrift.registry.NodeEvent.EventType;

/**
 * 注册、监听、解除注册、解除监听
 * 
 * @author justdebugit@gmail.com
 *
 */
public class ZkCuratorRegistry implements Registry {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZkCuratorRegistry.class);

	private final CuratorFactory curatorFactory;
	private final CuratorFramework client;
	private final NodeCacheManager nodeCacheManager;
	private static final Set<Type> ignoreTypes = ImmutableSet.of(
			Type.CONNECTION_SUSPENDED, Type.CONNECTION_RECONNECTED,
			Type.CONNECTION_LOST, Type.INITIALIZED);
	
	public ZkCuratorRegistry(CuratorFactory curatorFactory) {
		Preconditions.checkNotNull(curatorFactory);
		this.curatorFactory = curatorFactory;
		this.client = curatorFactory.getCuratorClient();
		nodeCacheManager = new NodeCacheManager();
	}

	@Override
	public void register(String path, byte[] bytes) {
		try {
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.EPHEMERAL).forPath(path, bytes);
		} catch (NodeExistsException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (Exception e) {
			throw new IllegalStateException(" can not create path " + path
					+ " " + e.getMessage(), e);
		}
	}

	@Override
	public void register(String path) {
		try {
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.EPHEMERAL).forPath(path);
		} catch (NodeExistsException e) {
			LOGGER.warn("node already Exists " + e.getMessage(), e);
		} catch (Exception e) {
			throw new IllegalStateException(" can not create path " + path
					+ " " + e.getMessage(), e);
		}
	}

	@Override
	public void unregister(String path) {
		try {
			client.delete().guaranteed().forPath(path);
		} catch (NoNodeException e) {
			LOGGER.warn("no node exists " + e.getMessage(), e);
		} catch (Exception e) {
			throw new IllegalStateException(" can not delete path " + path
					+ " " + e.getMessage(), e);
		}
	}

	@Override
	public void subscribe(String path, ChangeListener listener) {
		nodeCacheManager.addListener(path, listener);
	}

	@Override
	public void unsubscribe(String path, ChangeListener listener) {
		nodeCacheManager.removeListener(path, listener);
	}

	@Override
	public byte[] getData(String path) {
		NodeCache nodeCache = nodeCacheManager.nodeCacheMap.get(path);
		ChildData childData = null;
		if (nodeCache != null
				&& (childData = nodeCache.getCurrentData()) != null) {
			return childData.getData();
		}
		return null;
	}

	@Override
	public Map<String, byte[]> getChildren(String path) {
		PathChildrenCache pathChildrenCache = nodeCacheManager.childNodeCacheMap
				.get(path);
		List<ChildData> children = null;
		if (pathChildrenCache != null
				&& (children = pathChildrenCache.getCurrentData()) != null) {
			Map<String, byte[]> retMap = Maps.newHashMap();
			for (ChildData childData : children) {
				retMap.put(childData.getPath(), childData.getData());
			}
			return retMap;
		}
		return null;
	}

	/**
	 * 监听节点事件每个path存放一个nodeCache和childrenNodeCache，
	 * 通过nodeCache和childrenNodeCache不用关心重连、session失效等问题
	 *
	 */
	private class NodeCacheManager {
		final ConcurrentMap<String, NodeCache> nodeCacheMap = new ConcurrentHashMap<String, NodeCache>();
		final ConcurrentMap<String, PathChildrenCache> childNodeCacheMap = Maps
				.newConcurrentMap();
		final ConcurrentMap<ChangeListener, PathChildrenCacheListener> change2ChildListenerMap = Maps
				.newConcurrentMap();
		final ConcurrentMap<ChangeListener, NodeCacheListener> change2NodeListenerMap = Maps
				.newConcurrentMap();

		void addListener(String path, final ChangeListener listener) {
			addToNodeCache(path, listener);
			addToChildNodeCache(path, listener);
		}

		void removeListener(String path, final ChangeListener listener) {
			NodeCache nodeCache = nodeCacheMap.get(path);
			if (nodeCache != null) {
				nodeCache.getListenable().removeListener(
						change2NodeListenerMap.get(path));
			}
			PathChildrenCache pathChildrenCache = childNodeCacheMap.get(path);
			if (pathChildrenCache != null) {
				pathChildrenCache.getListenable().removeListener(
						change2ChildListenerMap.get(path));
			}
		}

		private void addToChildNodeCache(String path,
				final ChangeListener listener) {
			PathChildrenCache childrenCache = childNodeCacheMap.get(path);
			if (childrenCache == null) {
				childNodeCacheMap.putIfAbsent(path,
						curatorFactory.getPathChildrenCache(client, path));
				childrenCache = childNodeCacheMap.get(path);
			}
			PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {

				@Override
				public void childEvent(CuratorFramework client,
						PathChildrenCacheEvent event) throws Exception {
					if (ignoreTypes.contains(event.getType())) {
						return;
					}
					ImmutablePair<String, byte[]> pair = ImmutablePair
							.<String, byte[]> of(event.getData().getPath(),
									event.getData().getData());
					switch (event.getType()) {
					case CHILD_ADDED:
						listener.onChange(new NodeEvent(EventType.CHILD_ADDED,
								pair));
						break;
					case CHILD_UPDATED:
						listener.onChange(new NodeEvent(
								EventType.CHILD_UPDATED, pair));
						break;
					case CHILD_REMOVED:
						listener.onChange(new NodeEvent(
								EventType.CHILD_REMOVED, pair));
						break;
					default:
						break;
					}
				}
			};
			change2ChildListenerMap.putIfAbsent(listener,
					pathChildrenCacheListener);
			childrenCache.getListenable().addListener(
					change2ChildListenerMap.get(listener));
		}

		private void addToNodeCache(String path, final ChangeListener listener) {
			NodeCache nodeCache = nodeCacheMap.get(path);
			if (nodeCache == null) {
				nodeCacheMap.putIfAbsent(path,
						curatorFactory.getNodeCache(client, path));
				nodeCache = nodeCacheMap.get(path);
			}
			final NodeCache tmpCache = nodeCache;
			NodeCacheListener nodeCacheListener = new NodeCacheListener() {

				@Override
				public void nodeChanged() throws Exception {
					ChildData childData = tmpCache.getCurrentData();
					if (childData!=null) {
						ImmutablePair<String, byte[]> pair = ImmutablePair
								.<String, byte[]> of(childData.getPath(),
										childData.getData());
						listener.onChange(new NodeEvent(EventType.NODE_CHANAGED,
										pair));
					}else {
						listener.onChange(new NodeEvent(EventType.NODE_CHANAGED,
										null));
					}
				}
			};
			change2NodeListenerMap.putIfAbsent(listener, nodeCacheListener);
			nodeCache.getListenable().addListener(
					change2NodeListenerMap.get(listener));
		}
	}
}
