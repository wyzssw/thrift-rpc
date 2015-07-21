package com.justdebugit.thrift.example.thriftrpc;

import java.util.Map;

import org.apache.thrift.TProcessor;

import com.google.common.collect.ImmutableMap;
import com.justdebugit.thrift.example.raw.BlackServiceImpl;
import com.justdebugit.thrift.example.raw.UserManagerServiceImpl;
import com.justdebugit.thrift.generated.BlackService;
import com.justdebugit.thrift.generated.UserManagerService;
import com.justdebugit.thrift.provider.RpcProvider;


public class RpcProviderMain {
	public static void main(String[] args) {
		final Map<String, TProcessor> processorMap = ImmutableMap
				.<String, TProcessor> of(
						"blackService",
						new BlackService.Processor<BlackService.Iface>(
								new BlackServiceImpl()),
						"userService",
						new UserManagerService.Processor<UserManagerService.Iface>(
								new UserManagerServiceImpl()));
		final RpcProvider rpcProvider = RpcProvider.builder()
				.connectStringForzk("127.0.0.1:2181")
				.processorMap(processorMap).serverPort(8080).build();
		rpcProvider.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				rpcProvider.stop();
				
			}
		}));
	}
	
}
