package com.justdebugit.thrift.utils;

import com.justdebugit.thrift.registry.ChangeListener;
import com.justdebugit.thrift.registry.NodeEvent;
import com.justdebugit.thrift.registry.Registry;

/**
 * 提供registry工具类
 * @author justdebugit@gmail.com
 *
 */
public final class ZkRegistryUtils {
	private ZkRegistryUtils(){}
	
	/**
	 * 即便session丢失，重连时会重新注册
	 */
    public static void registerWithGuaranteed(final Registry registry,final String path,final byte[] bytes){
    	registry.register(path,bytes);
    	registry.subscribe(path, new ChangeListener() {
			
			@Override
			public void onChange(NodeEvent event) {
				switch (event.getType()) {
				case NODE_CHANAGED:
					if (event.getData()==null) {
						registry.register(path, bytes);
					}
					break;
				default:
					break;
				}
			}
		});
    };
}
