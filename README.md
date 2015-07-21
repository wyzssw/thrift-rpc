# thrift-rpc
基于Thrift的rpc框架---自动发现服务(借助zookeeper)，动态代理(减少代码编写)，高性能(client连接池使用fastpool)
# example

provider
```java
     Map<String, TProcessor> processorMap = ImmutableMap
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
```

consumer
```java
   	ServiceConfig blacklistService = new ServiceConfig();
	blacklistService.setServiceName("blackService")
			.setServiceClass(BlackService.class).setMinPoolSize(10);

	ServiceConfig userService = new ServiceConfig();
	userService.setServiceName("userService").setServiceClass(
			UserManagerService.class);

	final RpcConsumer rpcConsumer = RpcConsumerFactory.newRpcConsumer(
			"127.0.0.1:2181",
			Lists.newArrayList(blacklistService, userService));
	rpcConsumer.start();

	final BlackService.Iface client = rpcConsumer
			.getService(BlackService.Iface.class);
	System.out.println(client.isBlack(11));

	final UserManagerService.Iface userCliIface = rpcConsumer
			.getService(UserManagerService.Iface.class);
	System.out.println(userCliIface.get(1));
```

#性能测试
经测试，TPS 达到 2.08w/s
