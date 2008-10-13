package com.googlecode.jkota;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LogManager {

	public static String LOG_FILE=System.getProperty("user.home")+ File.separator+"jkota.log";
	private static LogManager theInstance=new LogManager();
	private Logger fileLogger,consoleLogger;
	private boolean debug=new Boolean(System.getProperty("debug"));
	private LogManager() {
		try {
			fileLogger= Logger.getLogger("file");
			FileHandler fileHandler =new FileHandler(LOG_FILE,true);
			fileHandler.setFormatter(new SimpleFormatter());
			fileHandler.setLevel(Level.ALL);
			fileLogger.addHandler(fileHandler);
			fileLogger.setLevel(Level.ALL);
			consoleLogger=Logger.getLogger("console");
			consoleLogger.setLevel(Level.ALL);
		} catch (SecurityException e) {
			consoleLogger.log(Level.SEVERE,"Kayıtçı alınamadı",e);
			System.exit(-1);
		} catch (IOException e) {
			consoleLogger.log(Level.SEVERE,"Kayıtçı alınamadı",e);
			System.exit(-1);
		}
	}

	public static LogManager getInstance() { return theInstance; }

	public void info(String message, boolean printToFile) {
		if(printToFile || debug)
			fileLogger.info(message);
		else
			consoleLogger.info(message);
	}

	public void info(String message) {
		info(message,false);
	}

	public void warning(String message, Exception e) {
		consoleLogger.log(Level.WARNING,message,e);
	}

	public void warning(String message,boolean printToFile) {
		if(printToFile || debug)
			fileLogger.log(Level.WARNING,message);
		else
			consoleLogger.log(Level.WARNING,message);
	}

	public void warning(String message) {
		warning(message,false);
	}
}
