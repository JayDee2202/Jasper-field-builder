package jasperfieldbuilder.util;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class PJFLogger extends Logger {

	protected PJFLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}
	
	
	public static synchronized Logger getLogger(String name) {
		LogManager manager = LogManager.getLogManager();
		Logger logger = manager.getLogger(name);
		if (logger == null) {
			logger = new PJFLogger(name, null);
			logger.setLevel(Level.SEVERE);
			manager.addLogger(logger);
		}
		return logger;
	}
}
