package com.justdebugit.thrift.example.thriftrpc;

import com.justdebugit.thrift.generated.BlackService;


public class Test {
	public static void main(String[] args) throws ClassNotFoundException {
		Class<?> clazz = BlackService.class;
		Class.forName(clazz.getName()+"$"+"Client");
	}

}
