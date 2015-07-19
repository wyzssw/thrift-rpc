package com.justdebugit.thrift.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.justdebugit.fastpool.Pool;

public interface StatefulPool<T> extends Pool<T> {
	
	
	AtomicReference<State> state();
	
	
	AtomicInteger getRefCnt();

}
