package com.kaer.more.http;

public class CommunicateConfig {
	public static boolean DevelopMode=false;
	private static final String HttpIP="http://114.115.173.50:8080/sw/";
	//private static final String HttpIP="http://192.168.6.251:8080/sw/";
	public static String GetHttpClientAdress()
	{
		return HttpIP;
	}
}