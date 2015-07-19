package com.justdebugit.thrift.pool;


public interface StatefulPoolFactory<T> {
	
	StatefulPool<T>  getPool(String info);
	
	
	Class<T>         getType();
	
	void             setType(Class<T> type);

}
