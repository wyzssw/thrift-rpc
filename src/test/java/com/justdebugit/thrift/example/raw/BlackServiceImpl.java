package com.justdebugit.thrift.example.raw;
import org.apache.thrift.TException;

import com.justdebugit.thrift.generated.*;
public class BlackServiceImpl implements BlackService.Iface {

	@Override
	public boolean isBlack(int uid) throws TException {
		return userManagerConstants.BLACKLIST.contains(uid);
	}

}
