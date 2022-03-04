package com.gegebot.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationLoader {
	
	public static String BOT_TOKEN;
	public static String BOT_ID;
	public static int SECONDS_BEFORE_POST;
	
	public static void loadConfig() {
		try {
			Configuration config = new PropertiesConfiguration("config.properties");
			BOT_TOKEN = config.getString("BOT_TOKEN");
			BOT_ID = config.getString("BOT_ID");
			SECONDS_BEFORE_POST = config.getInt("SECONDS_BEFORE_POST");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
}
