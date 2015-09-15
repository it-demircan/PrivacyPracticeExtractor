package services;

import java.io.IOException;

//TODO: Implement a logger
public class Logger {
	static TextWriter tw = new TextWriter();
	static StringBuffer allLogs = new StringBuffer();
	
	public static void error(String errMsg){
		System.out.println("[ERROR] "+ errMsg);
	}
	public static void info(String infoMsg){
		allLogs.append(infoMsg).append("\r\n");
		System.out.println("[INFO] "+ infoMsg);
	}
	
	public static void saveLog() throws IOException
	{
		tw.write(allLogs.toString(), "./log.txt");
	}
}
