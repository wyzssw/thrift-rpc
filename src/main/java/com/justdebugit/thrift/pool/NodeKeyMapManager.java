package com.justdebugit.thrift.pool;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.google.common.eventbus.Subscribe;
import com.justdebugit.thrift.common.LifeCycle;
import com.justdebugit.thrift.registry.NodeEvent;
import com.justdebugit.thrift.registry.Registry;
import com.justdebugit.thrift.registry.ZkPathConstants;


abstract class NodeKeyMapManager<V> implements LifeCycle{
	private final AtomicBoolean state = new AtomicBoolean();
	private final ConcurrentMap<String, V> objectMap = new ConcurrentHashMap<String, V>(); //nodePath -->对象 映射
	
	protected NotifiableLifeCycleMap configMap = null;//nodePath -> 节点数据 映射
	protected String serviceName;
	
	public NodeKeyMapManager(String serviceName,Registry registry){
		   String path = "/"+serviceName + ZkPathConstants.PROVIDER_SUFFIX_PATH;
		   this.configMap = new AjustingLifeCycleMap(path, registry);
	}

	@Override
	public void start() {
		if (state.compareAndSet(false, true)) {
			configMap.start();
			configMap.getEventBus().register(this);
			for (String  node : configMap.keySet()) {
				objectMap.putIfAbsent(node, createObject(node));
			}
		}
	}

	@Override
	public void stop() {
		if (state.compareAndSet(true, false)) {
			configMap.stop();
			configMap.getEventBus().unregister(this);
		}
	}

	@Override
	public boolean isStarted() {
		return state.get();
	}
	
	public V get(String key){
		return objectMap.get(key);
	}
	
	protected  ConcurrentMap<String, V> getObjectMap(){
		return objectMap;
	}
	
	
	
	@Subscribe
	protected  void  onChange(NodeEvent event){
		String key = event.getData().getKey();
		switch (event.getType()) {
		case CHILD_ADDED:
			onAdd(key);
			break;
		case CHILD_REMOVED:
			onRemove(key);
		default:
			break;
		}
		
	};
	
	protected abstract void  onAdd(String key);
	
	protected abstract void  onRemove(String key);
	
	protected abstract V     createObject(String key);
	
	protected abstract int   size();
	
    
}
