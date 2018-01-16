package com.code.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

public class RMIServer {
	public static void main(String[] args) {
		try {
			String host = InetAddress.getLocalHost().getHostAddress();
			int rmiPort = 8400;
			String bindName = "licen";
			
			System.setProperty("java.rmi.server.host", "192.168.0.160");
			try {
				RMISocketFactory.setSocketFactory(new MyRMISocket());
				LocateRegistry.createRegistry(rmiPort);
			} catch (IOException e) {
                System.out.println("服务器发生错误：" + e.getMessage());
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
			
	
	

}
