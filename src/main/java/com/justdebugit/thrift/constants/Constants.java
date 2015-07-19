package com.justdebugit.thrift.constants;

import java.util.regex.Pattern;

public class Constants {
	
	public static final Pattern COMMA_SPLIT_PATTERN = Pattern
			.compile("\\s*[,]+\\s*");

	public static final Integer DEFAULT_CONNECT_TIMEOUT = 6000;
	public static final Integer DEFAULT_SESSION_TIMEOUT = 12000;
	
}
