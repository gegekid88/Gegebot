package com.gegebot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import discord4j.core.object.entity.Member;

public class EventOrganizer {
	
	private Map<String, GuildEvent> guildEvents = new HashMap<>();

	public void createEvent(String guildId, String channelId, String eventId,
			EventConfiguration eventConfiguration) {

		GuildEvent guildEvent = this.guildEvents.get(guildId);
		if (guildEvent == null) {
			guildEvent = new GuildEvent();
		}

		guildEvent.createEvent(channelId, eventId, eventConfiguration);

		this.guildEvents.put(guildId, guildEvent);
	}

	public void signUpToEvent(String guildId, String eventId, String role, Member member) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		guildEvent.signUpToEvent(eventId, role, member);
	}

	public void leaveEvent(String guildId, String eventId, String role, Member member) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		guildEvent.leave(eventId, role, member);
	}

	public String getEventSpecificStringInfo(String guildId, String eventId) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		return guildEvent.getSpecificEventStringInfo(eventId);
	}
	
	public void cleanEvents() {
		for(Entry<String, GuildEvent> entry: this.guildEvents.entrySet()) {
			entry.getValue().cleanEvents();
		}
	}
	
	public List<Event> getRecurEvents(String guildId) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		if (guildEvent != null) {
			return guildEvent.getEventsToRecur();
		}
		return new ArrayList<>();
	}
}
