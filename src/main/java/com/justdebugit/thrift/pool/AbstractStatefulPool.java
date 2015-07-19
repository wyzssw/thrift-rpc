package com.justdebugit.thrift.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public  abstract class AbstractStatefulPool<T> implements StatefulPool<T> {
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNINITIALIZED);
    
    private final AtomicInteger refCnt = new AtomicInteger();

	@Override
	public AtomicReference<State> state() {
		return state;
	}

	@Override
	public AtomicInteger getRefCnt() {
		return refCnt;
	}
	
	protected boolean isInitialized(){
		return state.get()==State.INITIALIZED;
	}
	
	protected boolean hasRef(){
		return refCnt.get()>0;
	}

}
