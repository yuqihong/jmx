package com.code.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.sf.json.JSONObject;

public class JMXClient {

	public static void main(String[] args) throws IOException, InstanceNotFoundException, IntrospectionException,
			ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException {
		String url = null;
		int maxValue = 256;
		String[] name = new String[] {};
		String param = null;

		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("u", "url", true, "import url:远程端口 \t(1)rmi://host:port \t(2)jmxmp://host:port");
		options.addOption("z", "param", true, "param:参数 有空格在两端加引号表示; \t示例(1):java.lang:type=Threading 表示查询线程信息"
				+ "\t示例(2):“java.lang:type=MemoryPool,name=PS Old Gen” ");
		options.addOption("h", "help", false, "print help:帮助");
		options.addOption("n", "name", true, "属性名称");

		String formatstr = "[-u/--url][-z/--param][-h/--help][-m/--name] DirectoryName";
		HelpFormatter formatter = new HelpFormatter();

		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			formatter.printHelp(formatstr, options);
			e.printStackTrace();
		}

		/**
		 * 参数：-h/--help 帮助
		 */
		if (commandLine.hasOption('h')) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(formatstr, "", options, "");
			return;
		}

		/**
		 * url远程端口
		 */
		if (commandLine.hasOption('u')) {
			url = commandLine.getOptionValue('u');
		}

		/**
		 * 值：-z/--param
		 */
		if (commandLine.hasOption('z')) {
			param = commandLine.getOptionValue('z');
		}

		if (commandLine.hasOption('n')) {
			name = commandLine.getOptionValues('n');
		}
		/**
		 * 连接远程地址访问拼接
		 */
		// String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/" +
		// DOMA ; //rmi连接
		// String url = "service:jmx:jmxmp://" + host + ":" + port + "/" + DOMA; //
		// jmxmp连接
		if (url.indexOf("rmi") != -1 || url.indexOf("RMI") != -1) {
			url = "service:jmx:rmi:///jndi/" + url;
		}
		if (url.indexOf("jmxmp") != -1 || url.indexOf("JMXMP") != -1) {
			url = "service:jmx:" + url;
		}

		JMXServiceURL serviceURL = new JMXServiceURL(url);
		final JMXConnector connector;
		try {
			connector = JMXConnectorFactory.connect(serviceURL);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		/**
		 * objectName 信息检索
		 */

		JSONObject jsonObject = new JSONObject();
		MBeanServerConnection connection = connector.getMBeanServerConnection();
		ObjectName objectName = new ObjectName(param);
		MBeanInfo mBeanInfo = connection.getMBeanInfo(objectName);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		MBeanAttributeInfo[] mBeanAttrbute = mBeanInfo.getAttributes();
		if (name.length > 0) {
			for (int i = 0; i < name.length; i++) {
				for (MBeanAttributeInfo attr : mBeanAttrbute) {
					Object value = null;
					try {
						if (attr.getName().equals(name[i])) {
							Map<String, Object> map = new LinkedHashMap<String, Object>();
							value = attr.isReadable() ? connection.getAttribute(objectName, name[i]) : "";
							if (value.toString().isEmpty()) {
								break;
							} else {
								map.put("" + attr.getName() + "", String.valueOf(value));
								list.add(map);
								jsonObject.put("data", list);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			for (MBeanAttributeInfo attr : mBeanAttrbute) {
				Object value = null;
				try {
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					value = attr.isReadable() ? connection.getAttribute(objectName, attr.getName()) : "";
					if (value.toString().isEmpty() || value.toString().length() > maxValue) {
						break;
					} else {
						map.put("" + attr.getName() + "", String.valueOf(value));
						list.add(map);
						jsonObject.put("data", list);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(jsonObject);
		connector.close();
	}
}
