package com.justdebugit.thrift.common;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractLifeCycle implements LifeCycle{
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
