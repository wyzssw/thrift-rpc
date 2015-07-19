package com.justdebugit.thrift.registry;

public class ZkPathConstants {
	/**
	 * 格式约定:1 父节点/com.xxx.xxService/providers
	 *        2 子节点 192.168.137.1:8989
	 *                192.168.137.2:8989
	 *                192.168.137.3:8989
	 *        3 子节点代表提供服务的实例
	 */
	public static final String PROVIDER_SUFFIX_PATH = "/providers";
	
	
	/**
	 * 格式约定:1 父节点/com.xxx.xxService/consumers
	 *        2 子节点 192.168.137.1:8989
	 *                192.168.137.2:8989
	 *                192.168.137.3:8989
	 *        3 子节点代表消费服务的实例
	 */
	public static final String CONSUMER_SUFFIX_PATH = "/consumers";
	
	

}
