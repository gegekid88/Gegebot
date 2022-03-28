package com.gegebot.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

import com.gegebot.Main;
import com.gegebot.model.Event;
import com.gegebot.model.EventConfiguration;
import com.gegebot.model.EventOrganizer;
import com.gegebot.model.RoleConfiguration;
import com.gegebot.util.ConfigurationLoader;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventCleaner extends TimerTask {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	@NonNull
	private Timer timer;
	@NonNull
	private GatewayDiscordClient client;
	@NonNull
	private EventOrganizer eventOrganizer;

	@Override
	public void run() {

		List<Event> endingEvents = this.eventOrganizer.getEndingEvents();
		for (Event event : endingEvents) {

			EventConfiguration eventConfiguration = event.getEventConfiguration();

			// attempt to recur recurrent events
			if (eventConfiguration.getRecurTimeInSeconds() > 0) {
				recurEvents(eventConfiguration);
			}

			// remind people
			remind(event);

			// clean event
			cleanEvents(eventConfiguration);
		}
	}

	private void remind(Event event) {
		LOGGER.info("reminding " + event.getEventConfiguration().getEventId());
		
		List<Member> remindJoiners = new ArrayList<>();
		for (List<Member> joiners : event.getJoinersMap().values()) {
			remindJoiners.addAll(joiners);
		}
		String reminder = "Lets go go go to " + event.getEventConfiguration().getTitle() + " ";
		for (Member m : remindJoiners) {
			reminder += "<@" + m.getId().asString() + ">";
			reminder += " ";
		}

		((TextChannel) this.client.getChannelById(Snowflake.of(event.getEventConfiguration().getPartyMeetingChannelId())).block())
				.createMessage(reminder).block();
	}

	private void cleanEvents(EventConfiguration eventConfiguration) {
		LOGGER.info("cleaning " + eventConfiguration.getEventId());
		
		TimerTask task = new EventDeletor(client, eventConfiguration.getScheduledChannelId(),
				eventConfiguration.getEventId());
		Long delay = Integer.toUnsignedLong(ConfigurationLoader.SECONDS_BEFORE_DELETE);
		// do not delete post immediately so people can have reference of the party
		// during recruit for 6 hours
		timer.schedule(task, delay * 1000);
	}

	private void recurEvents(EventConfiguration eventConfiguration) {
		
		LOGGER.info("recurring " + eventConfiguration.getEventId());
		
		Map<String, Object> eventInfos = extractEventInfo(eventConfiguration);
		TimerTask task = new EventCreator(client, eventConfiguration.getScheduledChannelId(),
				(String) eventInfos.get("json"));

		Long delay = (Long) eventInfos.get("epoch");
		// if already less than 12 hours before actual event, post it immediately so
		// people can sign up!!
		delay = delay < 0 ? 0 : delay;
		timer.schedule(task, delay * 1000);
	}

	private Map<String, Object> extractEventInfo(EventConfiguration eventConfiguration) {

		Map<String, Object> infos = new HashMap<>();

		int epoch = eventConfiguration.getEventStartEpoch() + eventConfiguration.getRecurTimeInSeconds();

		String json = "";
		json += "{\"title\": \"" + eventConfiguration.getTitle() + "\"," + " \"scheduledChannelName\":\""
				+ eventConfiguration.getScheduledChannelName() + "\"," + " \"partyMeetingChannelName\":\""
				+ eventConfiguration.getPartyMeetingChannelName() + "\"," + " \"eventStartEpoch\":" + epoch + ", "
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
