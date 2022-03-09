package com.gegebot.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Timer;
import java.util.TimerTask;

import com.gegebot.model.Event;
import com.gegebot.model.EventConfiguration;
import com.gegebot.model.EventOrganizer;
import com.gegebot.model.RoleConfiguration;
import com.gegebot.util.ConfigurationLoader;

import discord4j.core.GatewayDiscordClient;

public class EventCleaner extends TimerTask {
	private Timer timer;
	private GatewayDiscordClient client;
	private EventOrganizer eventOrganizer;

	public EventCleaner(Timer timer, GatewayDiscordClient client, EventOrganizer eventOrganizer) {
		this.timer = timer;
		this.client = client;
		this.eventOrganizer = eventOrganizer;
	}

	@Override
	public void run() {
		// get all the guilds using the bot
		List<String> guildIds = new ArrayList<>();
		client.getGuilds().map(guild -> guild.getId().asString()).subscribe(guildIds::add);

		for (String guildId : guildIds) {
			List<Event> recurEvents = this.eventOrganizer.getRecurEvents(guildId);
			for (Event event : recurEvents) {
				EventConfiguration eventConfiguration = event.getEventConfiguration();
				Map<String, Object> eventInfos = extractEventInfo(eventConfiguration);
				TimerTask task = new EventCreator(client, event.getChannelId(), (String) eventInfos.get("json"));

				Long delay = (Long) eventInfos.get("epoch");
				// if already less than 12 hours before actual event, post it immediately so
				// people can sign up!!
				delay = delay < 0 ? 0 : delay;
				timer.schedule(task, delay * 1000);
			}
			
			List<Event> cleanEvents = this.eventOrganizer.cleanEvents(guildId);
			for (Event event : cleanEvents) {
				TimerTask task = new EventDeletor(client, event.getChannelId(), event.getEventId());

				Long delay = Integer.toUnsignedLong(ConfigurationLoader.SECONDS_BEFORE_DELETE);
				// do not delete post immediately so people can have reference of the party during recruit for 6 hours
				timer.schedule(task, delay * 1000);
			}
		}
	}

	private Map<String, Object> extractEventInfo(EventConfiguration eventConfiguration) {

		Map<String, Object> infos = new HashMap<>();

		int epoch = eventConfiguration.getEventStartEpoch() + eventConfiguration.getRecurTimeInSeconds();

		String json = "";
		json += "{\"title\": \"" + eventConfiguration.getTitle() + "\"," + " \"eventStartEpoch\":" + epoch + ", "
				+ "\"recurTimeInSeconds\":" + eventConfiguration.getRecurTimeInSeconds() + "," + "\"roles\": {";

		Map<String, RoleConfiguration> roleConfiguration = eventConfiguration.getRoleConfigurationMap();
		for (Entry<String, RoleConfiguration> entry : roleConfiguration.entrySet()) {
			json += "\"" + entry.getKey() + "\": {";
			json += "\"count\":" + entry.getValue().getCount() + ",";
			json += "\"emoji\":" + "\"" + entry.getValue().getEmojiUnicode() + "\"},";
		}

		json = json.substring(0, json.length() - 1);
		json += "}}";

		infos.put("json", json);

		// recreate the event x hours before the actual event will happen
		infos.put("epoch", epoch - System.currentTimeMillis() / 1000 - ConfigurationLoader.SECONDS_BEFORE_POST);
		
		return infos;
	}
}
