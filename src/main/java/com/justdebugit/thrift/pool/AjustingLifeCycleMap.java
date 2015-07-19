package com.justdebugit.thrift.pool;

import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdebugit.thrift.registry.ChangeListener;
import com.justdebugit.thrift.registry.NodeEvent;
import com.justdebugit.thrift.registry.Registry;

public class AjustingLifeCycleMap extends NotifiableLifeCycleMap{
	private static final long serialVersionUID = -6764469565224658815L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AjustingLifeCycleMap.class);
	
	
	private final Registry registry ;
	private final String   path;
	
	private volatile ChangeListener changeListener = null;
	
	
	public  AjustingLifeCycleMap(String path,Registry registry) {
		this.registry    = registry;
		this.path   = path;
		start();
	}


	@Override
	protected void doStart() {
		changeListener = new ChangeListener() {
				
				@Override
				public void onChange(NodeEvent event) {
					   ImmutablePair<String, byte[]> data = event.getData();
					   switch (event.getType()) {
						case CHILD_ADDED:
							putIfAbsent(data.getKey(),data.getValue());
							break;
		                case CHILD_REMOVED:
		                	remove(data.getKey());
		                	break;
		                case CHILD_UPDATED:
		                	put(data.getKey(),data.getValue());
		                	break;
						default:
							break;
						}
					   getEventBus().post(event);
					}
		};
		registry.subscribe(path, changeListener);
		Map<String, byte[]> map = registry.getChildren(path);
		if (map!=null) {
			putAll(map);
		}else {
			LOGGER.warn("path not exists");
			registry.register(path);
		}
	}

	@Override
	protected void doStop() {
		if (changeListener==null) {
			registry.unsubscribe(path, changeListener);
		}
		
	}

}
