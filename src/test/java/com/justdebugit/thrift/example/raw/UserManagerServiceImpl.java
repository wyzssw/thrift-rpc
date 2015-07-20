package com.justdebugit.thrift.example.raw;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.thrift.TException;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.justdebugit.thrift.generated.DifferentSourceDetectedException;
import com.justdebugit.thrift.generated.UserInfo;
import com.justdebugit.thrift.generated.UserManagerService;

public class UserManagerServiceImpl implements UserManagerService.Iface{
	//所有注册用户
	static ConcurrentMap<Integer, UserInfo> map = new ConcurrentHashMap<Integer, UserInfo>();
	static ConcurrentMap<Integer, Set<UserInfo>> friendsMap = new ConcurrentHashMap<Integer, Set<UserInfo>>();
	static{
		UserInfo userInfo = new UserInfo(1);
		userInfo.score = 10.2;
		StringBuilder stringBuilder  = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			stringBuilder.append("a");
		}
		userInfo.name = stringBuilder.toString();
		UserInfo userInfo2 = new UserInfo(userInfo);
		userInfo2.setName("test2");
		userInfo2.setUid(2);
		UserInfo userInfo3 = new UserInfo(userInfo);
		userInfo3.setUid(3);
		userInfo3.setName("test3");
		map.put(1, userInfo);
		map.put(2, userInfo2);
		map.put(3, userInfo3);
		friendsMap.put(1, Sets.newHashSet(userInfo2,userInfo3));
		friendsMap.put(2, Sets.newHashSet(userInfo));
	}

	@Override
	public UserInfo get(int uid) throws TException {
		return map.get(uid);
	}

	@Override
	public void put(int uid, UserInfo info) throws TException {
		map.put(uid, info);
		
	}

	@Override
	public Set<UserInfo> getMyFriends(int uid) throws TException {
		return friendsMap.get(uid);
	}

	@Override
	public void defriend(int uid1, int uid2) throws TException {
		Set<UserInfo> list =  friendsMap.get(uid1);
		if (list!=null) {
			synchronized (list) {
				UserInfo info = map.get(uid2);
				if (info==null) {
					throw new TException("user "+uid2 +" is not exist");
				}
				list.remove(info);
			}
		}
	}

	@Override
	public boolean addfriend(int uid1, int uid2) throws TException {
		Set<UserInfo> list =  friendsMap.get(uid1);
		if (list!=null) {
			synchronized (list) {
				UserInfo info = map.get(uid2);
				if (info==null) {
					throw new TException("user "+uid2 +" is not exist");
				}
				list.add(info);
			}
		}else {
			friendsMap.putIfAbsent(uid1, Sets.<UserInfo>newHashSet());
			return false;
		}
		return true;
	}

	@Override
	public int compare(int uid1, int uid2)
			throws DifferentSourceDetectedException, TException {
		UserInfo userInfo1 = map.get(uid1);
		UserInfo userInfo2 = map.get(uid2);
		if (userInfo1==null||userInfo2==null) {
			throw new TException("user not found");
		}
		if (!Objects.equal(userInfo1.getSource(), userInfo2.getSource())) {
			throw new DifferentSourceDetectedException(uid1, uid2, "not same source");
		}
		return userInfo1.getScore() > userInfo2.getScore()?uid1:uid2;
	}


}
