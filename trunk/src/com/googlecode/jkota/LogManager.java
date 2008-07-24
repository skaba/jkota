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
	private Logger logger,debugger;
	private LogManager() {
		try {
			logger= Logger.getLogger("JKota");
			FileHandler fileHandler =new FileHandler(LOG_FILE,true);
			fileHandler.setFormatter(new SimpleFormatter());
			fileHandler.setLevel(Level.ALL);
			logger.addHandler(fileHandler);
			logger.setLevel(Level.ALL);
			debugger=Logger.getLogger("JKota");
			logger.setLevel(Level.ALL);
		} catch (SecurityException e) {
			logger.log(Level.SEVERE,"Kayıtçı alınamadı",e);
			System.exit(-1);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"Kayıtçı alınamadı",e);
			System.exit(-1);
		}
	}

	public void debug(String message,Exception e) {
		debugger.log(Level.WARNING,message,e);
	}

	public void debug(String message) {
		debugger.log(Level.WARNING,message);
	}

	public void info(String message) {
		logger.info(message);
	}

	public static LogManager getInstance() { return theInstance; }
}
