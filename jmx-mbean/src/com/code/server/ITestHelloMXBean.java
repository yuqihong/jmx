package com.code.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITestHelloMXBean extends Remote{

	public void hello() throws RemoteException;

	public String getName() throws RemoteException;
}
