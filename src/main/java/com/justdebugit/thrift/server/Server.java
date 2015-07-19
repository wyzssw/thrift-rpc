package com.justdebugit.thrift.server;

import java.io.IOException;

public interface Server {
		public abstract void start() throws IOException;

		public abstract void stop();

}
