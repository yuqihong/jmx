package com.code.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

public class JMXServer {

	private static final String DOMA = "jmxrmi";
	private static final String PACK = "org.suren.littlebird:type=";

	public static void main(String[] args) throws MalformedObjectNameException, InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException, IOException {
		LocateRegistry.createRegistry(5007);
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		TestHello testHello = new TestHello();
		ObjectName testHelloObjectName = new ObjectName(PACK + "TestHello");

		server.registerMBean(testHello, testHelloObjectName);

		JMXServiceURL serverUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:5007/" + DOMA);
		//JMXServiceURL serverUrl = new JMXServiceURL("service:jmx:jmxmp://127.0.0.1:5006/" + DOMA); // jmxmp连接

		Map<String, String> env = new HashMap<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory"); //rmi
		//env.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_PACKAGES, "com.sun.jmx.remote.protocol.jmxmp");

		JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serverUrl, env, server);
		server.registerMBean(connectorServer, new ObjectName(PACK + "JMXConnectorServer"));

		connectorServer.start();

	}

}
