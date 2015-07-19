package com.justdebugit.thrift.pool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.justdebugit.thrift.common.LifeCycle;


public abstract  class LifeCycleMap extends ConcurrentHashMap<String, byte[]> implements LifeCycle{

	private static final long serialVersionUID = -4277180793264814684L;
	
	protected AtomicBoolean state = new AtomicBoolean(false);
	
	public boolean isStarted(){
		return state.get();
	};
	
	public void start(){
		if (state.compareAndSet(false, true)) {
			doStart();
		}
	}

	public void stop(){
		if (state.compareAndSet(true, false)) {
			doStop();
		}
	}
       
	protected abstract void doStart();
	
	protected abstract void doStop();
}