package com.justdebugit.thrift.example.thriftrpc;

import org.apache.thrift.TException;

import com.google.common.collect.Lists;
import com.justdebugit.thrift.common.ServiceConfig;
import com.justdebugit.thrift.consumer.RpcConsumer;
import com.justdebugit.thrift.consumer.RpcConsumerFactory;
import com.justdebugit.thrift.generated.BlackService;
import com.justdebugit.thrift.generated.UserManagerService;

public class RpcConsumerMain {
   public static void main(String[] args) throws TException {
	   ServiceConfig blacklistService = new ServiceConfig();
	   blacklistService.setServiceName("blackService").setServiceClass(BlackService.class).setMinPoolSize(10);
	   
	   ServiceConfig userService = new ServiceConfig();
	   userService.setServiceName("userService").setServiceClass(UserManagerService.class);
	   
	   
	   final RpcConsumer rpcConsumer= RpcConsumerFactory.newRpcConsumer("127.0.0.1:2181", Lists.newArrayList(blacklistService,userService));
	   rpcConsumer.start();
	   
	   final BlackService.Iface client =  rpcConsumer.getService(BlackService.Iface.class);
	   System.out.println(client.isBlack(11));
	   
	   final UserManagerService.Iface userCliIface = rpcConsumer.getService(UserManagerService.Iface.class);
	   System.out.println(userCliIface.get(1));
	   
	  
	   Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		
		@Override
		public void run() {
			rpcConsumer.stop();
		}
	}));
   }
}
