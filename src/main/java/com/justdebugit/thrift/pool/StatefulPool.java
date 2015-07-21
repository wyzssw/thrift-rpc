package com.justdebugit.thrift.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.justdebugit.fastpool.Pool;
/**
 * 有状态的pool，支持计算器、状态变更
 * @author justdebugit@gmail.com
 *
 * @param <T>
 */
public interface StatefulPool<T> extends Pool<T> {
	
	
	AtomicReference<State> state();
	
	
	AtomicInteger getRefCnt();

}
