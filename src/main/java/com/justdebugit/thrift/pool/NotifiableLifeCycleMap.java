package com.justdebugit.thrift.pool;

import com.google.common.eventbus.EventBus;

public abstract class NotifiableLifeCycleMap extends LifeCycleMap{
	private static final long serialVersionUID = -4004240708096262886L;
	private final  EventBus eventBus = new EventBus("MAP-CHANGED");
	
	public EventBus getEventBus(){
		return eventBus;
	}
}
