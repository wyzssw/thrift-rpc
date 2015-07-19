package com.justdebugit.thrift.pool;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdebugit.thrift.registry.Registry;

public class StatefulPoolMapManager<T> extends NodeKeyMapManager<StatefulPool<T>>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StatefulPoolFactory.class);
	
	private StatefulPoolFactory<T> poolFactory;

	public StatefulPoolMapManager(String serviceName, Registry registry,StatefulPoolFactory<T> poolFactory) {
		super(serviceName, registry);
		this.poolFactory = poolFactory;
	}
	

	@Override
	protected  void onAdd(String key) {
		getObjectMap().put(key, createObject(key));
	}

	@Override
	protected  void onRemove(String key) {
	   StatefulPool<T> statefulPool = getObjectMap().get(key);
	   statefulPool.state().set(State.DESTROYED);
	}
	
	
	public StatefulPool<T> get(String key){
		StatefulPool<T> statefulPool =  getObjectMap().get(key);
		if (statefulPool.state().get()==State.DESTROYED && statefulPool.getRefCnt().get()<=0) {
			try {
				statefulPool.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
			}
			getObjectMap().remove(key);
			return null;
		}
		return statefulPool;
	}
	
	@Override
	protected StatefulPool<T> createObject(String key) {
		return poolFactory.getPool(key);
	}
	

	@Override
	protected int size() {
		int sum = 0;
		ConcurrentMap<String, StatefulPool<T>> map  = getObjectMap();
		for (Map.Entry<String, StatefulPool<T>> entry : map.entrySet()) {
			 sum += entry.getValue().size();
		}
		return sum;
	}
}
