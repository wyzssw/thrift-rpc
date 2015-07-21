package com.justdebugit.thrift.example.raw;

import java.io.IOException;

import org.apache.thrift.TException;

/**
 * Hello world!
 *
 */
public class RawThriftClientExample 
{
    public static void main( String[] args ) throws IOException, TException, InterruptedException
    {   
        final BlackServiceStub blackServiceStub = new BlackServiceStub("127.0.0.1",8080);
        System.out.println(blackServiceStub.isBlack(11));
        final UserManagerServiceStub userManagerServiceStub = new UserManagerServiceStub("127.0.0.1", 8080);
        userManagerServiceStub.addfriend(2, 3);
        userManagerServiceStub.compare(1, 2);
        userManagerServiceStub.defriend(2, 1);
        System.out.println(userManagerServiceStub.get(2));
        System.out.println(userManagerServiceStub.getMyFriends(2));
        Thread.sleep(Integer.MAX_VALUE);
    }
}
