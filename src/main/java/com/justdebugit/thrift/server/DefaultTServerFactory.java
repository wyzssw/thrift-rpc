package com.justdebugit.thrift.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.justdebugit.thrift.common.ThriftInitException;

public class DefaultTServerFactory implements TServerFactory {
	private final Map<String,TProcessor> map;
	private final InetSocketAddress address;
	
	public DefaultTServerFactory(Map<String, TProcessor> processorMap,InetSocketAddress address) {
	    this.map = processorMap;
	    this.address = address;   
	}

	@Override
	public TServer createTServer() {
		TMultiplexedProcessor tMultiplexedProcessor = new TMultiplexedProcessor();
		 for (Map.Entry<String, TProcessor> entry : this.map.entrySet()) {
			   tMultiplexedProcessor.registerProcessor(entry.getKey(), entry.getValue());
		   }
		TNonblockingServerSocket socket = null;
		try {
			socket = new TNonblockingServerSocket(address);
		} catch (TTransportException e) {
			throw new ThriftInitException(e.getMessage(), e);
		}

		TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(
				socket);
		args.executorService(Executors.newFixedThreadPool(20));
		args.protocolFactory(new TCompactProtocol.Factory());
		args.processor(tMultiplexedProcessor);
		args.transportFactory(new TFramedTransport.Factory());
		TThreadedSelectorServer server = new TThreadedSelectorServer(args);
        return server;
	}

}
