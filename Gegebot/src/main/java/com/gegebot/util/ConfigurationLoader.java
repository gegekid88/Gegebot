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
	public static int SECONDS_BEFORE_DELETE;
	
	public static void loadConfig() {
		try {
			Configuration config = new PropertiesConfiguration("config.properties");
			String token = config.getString("BOT_TOKEN");
			BOT_TOKEN = !token.isEmpty() ? token : System.getenv("BOT_TOKEN");
			String botId = config.getString("BOT_ID");
			BOT_ID = !botId.isEmpty() ? botId : System.getenv("BOT_ID");
			String secondsBeforePost = config.getString("SECONDS_BEFORE_POST");
			SECONDS_BEFORE_POST = !secondsBeforePost.isEmpty() ? Integer.parseInt(secondsBeforePost) : 
				Integer.parseInt(System.getenv("SECONDS_BEFORE_POST"));
			String secondsBeforeDelete = config.getString("SECONDS_BEFORE_DELETE");
			SECONDS_BEFORE_DELETE = !secondsBeforeDelete.isEmpty() ? Integer.parseInt(secondsBeforeDelete) : 
				Integer.parseInt(System.getenv("SECONDS_BEFORE_DELETE"));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
}
