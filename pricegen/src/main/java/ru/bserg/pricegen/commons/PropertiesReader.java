package ru.bserg.pricegen.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Помогает читать properties из файла name в classpath
 * @author boychenko_sn
 */
public class PropertiesReader {
	private static final Logger logger = Logger.getLogger(PropertiesReader.class.getName());
	/** Имя файла*/
	private static final String NAME = "config.properties";
	private static final String SEPARATOR = ",";
	private static Properties properties = null;
	
	private static Properties init() {
		logger.fine("Initializating properties-file...");
		try {
			//InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(NAME);
			InputStream inputStream = new FileInputStream(new File(NAME));
			Properties properties = new Properties();  
	        properties.load(new InputStreamReader(inputStream, "UTF8"));
	        logger.fine("Initializating properties-file complete");
	        return properties;
		} catch (IOException e) {
			logger.warning("Initializating error: "+e.getMessage());
			return new Properties();
		} 
	}
	
	public static void set(String key, String value) {
		if (key != null) {
			if (value == null) value = ""; 
			properties.setProperty(key, value);
		}
	}
	public static void store() {
		try {
			properties.store(new FileOutputStream(new File(NAME)), "PriceGen configuration");
			//properties = null;
		} catch (Exception e) {
			logger.warning("Initializating error: "+e.getMessage());
		}
	}
	
	public static String get(String key) {
		logger.finest("Get " + key + " from properties");
		if (properties == null) {
			properties = init();
		}
		logger.finest("Return: "+properties.getProperty(key));
		return properties.getProperty(key);
	}
	
	public static String get(String key, String def) {
		logger.finest("Get " + key + " (default: " + def + ") from properties");
		if (properties == null) {
			properties = init();
		}
		logger.finest("Return: "+properties.getProperty(key, def));
		return properties.getProperty(key, def);
	}
	
	public static int get(String key, int def) {
		logger.finest("Get " + key + " (default: " + def + ") from properties");
		if (properties == null) {
			properties = init();
		}
		String value = properties.getProperty(key, String.valueOf(def));
		
		try {
			int result = Integer.parseInt(value);
			logger.finest("Return: "+result);
			return result;				
		} catch (NumberFormatException e) {
			logger.finest("Return (NumberFormatException): "+def);
			return def;
		}
	}
	
	public static String[] getArray(String key) {
		logger.finest("Get array " + key + " from properties");
		if (properties == null) {
			properties = init();
		}
		String value = properties.getProperty(key);
		
		if (value == null) {
			logger.finest("Return: null");
			return null;
		}
		
		String[] result;
		result = value.split(SEPARATOR);
		logger.finest("Return: "+result);
		return result;				
	}
	
	public static Set<String> getSet(String key) {
		logger.finest("Get set " + key + " from properties");
		if (properties == null) {
			properties = init();
		}
		String value = properties.getProperty(key);
		
		if (value == null) {
			logger.finest("Return: null");
			return null;
		}
		
		HashSet<String> result = new HashSet<String>();
		if ("".equals(value)) {
			logger.finest("Return: "+result);
			return result;
		}
		
		for (String i : value.split(SEPARATOR)) {
			result.add(i);
		}
		logger.finest("Return: "+result);
		return result;				
	}
}
