package com.justdebugit.thrift.proxy;

import com.justdebugit.fastpool.Pool;
import com.justdebugit.thrift.bytecode.Proxy;

/**
 * PooledProxyFactory 

 * @author justdebugit
 */
public final class PooledProxyFactory  {

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> interfaceClass,Class<? extends T> clientClass,Pool<T> pool) {
        return (T) Proxy.getProxy(interfaceClass).newInstance(new PooledTargetInvocationHandler<T>(clientClass,pool));
    }

}