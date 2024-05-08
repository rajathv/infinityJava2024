package com.temenos.dbx.party.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PartyConfiguration {

	private static final Logger LOG = LogManager.getLogger(PartyConfiguration.class);
	private static Properties configProps = new Properties();

	static {
		try (InputStream inputStream = PartyURLFinder.class.getClassLoader()
				.getResourceAsStream("PartyConfiguration.properties");) {
			configProps.load(inputStream);
		} catch (FileNotFoundException e) {
			LOG.info("error while reading properties file", e);
		} catch (IOException e) {
			LOG.info("error while reading properties file", e);
		}
	}

	public static String getConfiguration(String key) {
		if (configProps.containsKey(key)) {
			return configProps.getProperty(key).trim();
		}
		return "";
	}

}
