package com.justdebugit.thrift.pool;

/**
 * 创建statefulPool工厂类
 * @author justdebugit@gmail.com
 *
 * @param <T>
 */
public interface StatefulPoolFactory<T> {
	
	StatefulPool<T>  getPool(String info);
	
	
	Class<T>         getType();
	
	void             setType(Class<T> type);

}
