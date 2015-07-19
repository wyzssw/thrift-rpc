package com.justdebugit.thrift.server;

import org.apache.thrift.server.TServer;

public interface TServerFactory {
	
	 TServer createTServer();

}
