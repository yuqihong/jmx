package com.code.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class MyRMISocket extends RMISocketFactory{

	public Socket createServerSocket(String host, int port) throws IOException {
		return new Socket(host, port);
	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		if (port == 0) {
			port = 5009;
		}
		return new ServerSocket(port);
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return null;
	}

	
}
