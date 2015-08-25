package services;

//TODO: Implement a logger
public class Logger {
	public static void error(String errMsg){
		System.out.println("[ERROR] "+ errMsg);
	}
	public static void info(String infoMsg){
		System.out.println("[INFO] "+ infoMsg);
	}
}
