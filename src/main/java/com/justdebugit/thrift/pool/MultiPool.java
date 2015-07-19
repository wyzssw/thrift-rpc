package com.justdebugit.thrift.pool;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.justdebugit.fastpool.Pool;
import com.justdebugit.thrift.common.LifeCycle;

public class MultiPool<T>  implements Pool<T>,LifeCycle {
	
	
    private static final LoadBalance DEFAULT_LOAD_BALANCE = new RandomPolicy();
    private AtomicBoolean state = new AtomicBoolean(false);
	private final StatefulPoolMapManager<T> path2pool;
	private final Cache<T,StatefulPool<T>> cache =  CacheBuilder.newBuilder().weakKeys().build();
	private LoadBalance loadBalance;
	
	
	public  MultiPool(StatefulPoolMapManager<T> path2pool,LoadBalance loadBalance) {
		this.path2pool = path2pool;
		this.loadBalance = loadBalance;
		start();
	}
	
	public  MultiPool(StatefulPoolMapManager<T> path2pool) {
		this(path2pool, DEFAULT_LOAD_BALANCE);
	}
	
	
	
	@Override
	public T get() throws InterruptedException {
		ImmutablePair<String, StatefulPool<T>> pair  = getPool();
		if (pair==null) {
			throw new IllegalPoolStateException("there is no pool available");
		}
		T entry = null;
		StatefulPool<T> pool = pair.getValue();
		cache.put(entry=pool.get(), pool);
		pool.getRefCnt().incrementAndGet();
		return entry;
	}

	@Override
	public T get(long timeout, TimeUnit timeUnit) throws InterruptedException,
			TimeoutException {
		T entry = null;
		ImmutablePair<String, StatefulPool<T>> pair = getPool();
		if (pair==null) {
			throw new IllegalPoolStateException("there is no pool available");
		}
		StatefulPool<T> pool = pair.getValue();
		cache.put(entry=pool.get(timeout,timeUnit), pool);
		pool.getRefCnt().incrementAndGet();
		return entry;
	}

	


	@Override
	public void release(T entry, boolean broken) {
		StatefulPool<T> statefulPool = cache.getIfPresent(entry);
		if (statefulPool!=null) {
			try {
				statefulPool.release(entry,broken);
			} finally{
				statefulPool.getRefCnt().decrementAndGet();
			}
		}
	}

	@Override
	public void release(T entry) {
		StatefulPool<T> statefulPool = cache.getIfPresent(entry);
		if (statefulPool!=null) {
			try {
				statefulPool.release(entry);
			} finally{
				statefulPool.getRefCnt().decrementAndGet();
			}
		}
	}

	@Override
	public int size() {
		return path2pool.size();
	}

	@Override
	public void scale(int size) {
		throw new UnsupportedOperationException();
	}
	
	
	
	private ImmutablePair<String, StatefulPool<T>>  getPool() {
		Map<String, StatefulPool<T>> objectMap = path2pool.getObjectMap();
		StatefulPool<T> foundPool = null;
		for (int i = 0; i < objectMap.size(); i++) {
			String key = loadBalance.select(Lists.newArrayList(objectMap
					.keySet()));
			foundPool = path2pool.get(key);
			if (foundPool == null
					|| foundPool.state().get() != State.INITIALIZED) {
				continue;
			} else {
				return ImmutablePair.of(key, foundPool);
			}
		}
		throw new IllegalPoolStateException(
				" thers is no useful pool in the container");
	}
		
	
	public static class RandomPolicy implements LoadBalance{
        Random random = new Random();
		@Override
		public String select(List<String> list) {
			if (list.size()<1) {
				throw new IllegalPoolStateException(" pool size must greater than one");
			}
			return list.get(random.nextInt(list.size()));
		}
		
	}
	
	public interface LoadBalance {
           String   select(List<String> list);
	}

	@Override
	public void start() {
		if (state.compareAndSet(false, true)) {
			path2pool.start();
		}
	}

	@Override
	public void stop() {
		if (state.compareAndSet(true, false)) {
			path2pool.stop();
			try {
				close();
			} catch (IOException e) {
				
			}
		};
	}

	@Override
	public boolean isStarted() {
		return state.get();
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
}
