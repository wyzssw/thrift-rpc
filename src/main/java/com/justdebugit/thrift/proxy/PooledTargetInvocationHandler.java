package com.justdebugit.thrift.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.justdebugit.fastpool.Pool;
import com.justdebugit.thrift.bytecode.Wrapper;


public class PooledTargetInvocationHandler<T> implements InvocationHandler {
	
	private final  Wrapper wrapper;
	
	private final Pool<T> pool;
	
	public PooledTargetInvocationHandler(Class<? extends T> clientClass,Pool<T> pool) {
		 this.wrapper = Wrapper.getWrapper(clientClass);
		 this.pool = pool;
	}
	

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		T target = pool.get();
		try {
			result = wrapper.invokeMethod(target, method.getName(), method.getParameterTypes(), args);
		}
		finally{
			pool.release(target);
		}
		return result;
		
	}

}
