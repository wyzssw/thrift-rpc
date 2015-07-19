package com.justdebugit.thrift.registry;

import java.util.Map;


/**
 * 提供注册中心注册与订阅功能接口
 * @author justdebugit@gmail.com
 *
 */
public interface Registry {
	
	/**
	 * 注册数据到path指定的节点上，没有就创建
	 * @param path   路径
	 * @param bytes  数据
	 */
	void register(String path,byte[] bytes);
	
	

	/**
	 * 注册数据到path指定的节点上，数据使用默认
	 * @param path   路径
	 * @param bytes  数据 默认数据
	 */
	void register(String path);
	
	/**
	 * 解除注册
	 * @param path 路径
	 */
	void unregister(String path);
	
	
	/**
	 * 订阅，绑定监听器
	 * @param path
	 * @param listener
	 */
	void subscribe(String path,ChangeListener listener);
	
	/**
	 * 解除订阅，解除监听器
	 * @param path
	 * @param listener
	 */
	void unsubscribe(String path,ChangeListener listener);
	
	/**
	 * 获取节点数据
	 * @param path 节点路径
	 * @return
	 */
	byte[]       getData(String path);
	
	/**
	 * 获取子节点数据
	 * @param path 父节点路径
	 * @return
	 */
	Map<String, byte[]> getChildren(String path);

}
