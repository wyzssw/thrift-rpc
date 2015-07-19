package com.justdebugit.thrift.pool;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.justdebugit.fastpool.Pool;

public class DelegatableStatefulPool<T> extends AbstractStatefulPool<T>{
	
	private Pool<T> pool;
	
	public  DelegatableStatefulPool(Pool<T> pool) {
		this.pool = pool;
	}

	@Override
	public T get() throws InterruptedException, IllegalPoolStateException {
		if (!isInitialized()) {
			throw new IllegalPoolStateException("pool is either uninitialized or destroyed");
		}
		return pool.get();
	}

	@Override
	public T get(long timeout, TimeUnit timeUnit) throws InterruptedException,
			TimeoutException, IllegalPoolStateException {
		if (!isInitialized()) {
			throw new IllegalPoolStateException("pool is either uninitialized or destroyed");
		}
		return pool.get();
	}

	@Override
	public void release(T t, boolean broken) {
		pool.release(t, broken);
	}

	@Override
	public void release(T t) {
		pool.release(t);
		
	}

	@Override
	public int size() {
		return pool.size();
	}

	@Override
	public void scale(int size) throws IllegalPoolStateException{
		if (!isInitialized()) {
			throw new IllegalPoolStateException("pool is either uninitialized or destroyed");
		}
		pool.scale(size);
	}

	@Override
	public void close() throws IOException {
		pool.close();
	}

}
