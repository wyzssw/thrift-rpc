package com.justdebugit.thrift.example.raw;

import java.io.IOException;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.justdebugit.thrift.generated.DifferentSourceDetectedException;
import com.justdebugit.thrift.generated.UserInfo;
import com.justdebugit.thrift.generated.UserManagerService;

public class UserManagerServiceStub implements  UserManagerService.Iface{

	private final UserManagerService.Client client ;
	
	
	public UserManagerServiceStub(String host,int port) throws IOException, TTransportException {
		TFramedTransport transport = new TFramedTransport(new TSocket(host,
				port));
		transport.open();
        client = new UserManagerService.Client(new TMultiplexedProtocol(new TCompactProtocol(transport), "userService"));
	}

	@Override
	public UserInfo get(int uid)  {
		try {
			return client.get(uid);
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void put(int uid, UserInfo info) throws TException {
		client.put(uid, info);
	}

	@Override
	public Set<UserInfo> getMyFriends(int uid) throws TException {
		return client.getMyFriends(uid);
	}

	@Override
	public void defriend(int uid1, int uid2) throws TException {
		client.defriend(uid1, uid2);
	}

	@Override
	public boolean addfriend(int uid1, int uid2) throws TException {
		return client.addfriend(uid1, uid2);
	}

	@Override
	public int compare(int uid1, int uid2)
			throws DifferentSourceDetectedException, TException {
		return client.compare(uid1, uid2);
	}

}
