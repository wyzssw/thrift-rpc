package com.justdebugit.thrift.example.raw;

import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.justdebugit.thrift.generated.BlackService;



public class BlackServiceStub implements BlackService.Iface{
	private final BlackService.Client client ;
	
	public BlackServiceStub(String host,int port) throws IOException, TTransportException{
		TFramedTransport transport = new TFramedTransport(new TSocket(host,
				port));
		transport.open();
        client = new BlackService.Client(new TMultiplexedProtocol(new TCompactProtocol(transport), "blackService"));
	}

	@Override
	public boolean isBlack(int uid) throws TException {
		return client.isBlack(uid);
	}


	
	

}
