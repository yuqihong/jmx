package com.code.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TestHello extends UnicastRemoteObject implements ITestHelloMXBean {
	private static final long serialVersionUID = 1L;
	
	protected TestHello() throws RemoteException {
		super();
	}

	@Override
	public void hello() throws RemoteException{
       System.out.println("hello world");		
	}

	@Override
	public String getName() throws RemoteException{
		return "test get name";
	}

}
