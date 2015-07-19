package com.justdebugit.thrift.example.raw;

import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.google.common.collect.ImmutableMap;
import com.justdebugit.thrift.generated.BlackService;
import com.justdebugit.thrift.generated.UserManagerService;

public class RawThriftServerExample {
	public static void main(String[] argsa) throws TTransportException  {
		 final Map<String, TProcessor> defaultMap = ImmutableMap
				.<String, TProcessor> of("blackService",
						new BlackService.Processor<BlackService.Iface>(
								new BlackServiceImpl()), "userService",
						new UserManagerService.Processor<UserManagerService.Iface>(
								new UserManagerServiceImpl()));
		TMultiplexedProcessor tMultiplexedProcessor = new TMultiplexedProcessor();
		TNonblockingServerSocket socket = new TNonblockingServerSocket(8080);
		TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(socket);
		args.executorService(Executors.newFixedThreadPool(20));
		args.protocolFactory(new TCompactProtocol.Factory());
		args.processor(tMultiplexedProcessor);
		args.transportFactory(new TFramedTransport.Factory());
		TThreadedSelectorServer server = new TThreadedSelectorServer(args);
		 for (Map.Entry<String, TProcessor> entry : defaultMap.entrySet()) {
			   tMultiplexedProcessor.registerProcessor(entry.getKey(), entry.getValue());
		   }
		server.serve();

	}

}
