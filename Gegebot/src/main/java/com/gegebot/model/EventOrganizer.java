package com.gegebot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import discord4j.core.object.entity.Member;

//todo null pattern instead of null checking
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
	
	public boolean deleteEvent(String guildId, String eventId) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		if (guildEvent != null) {
			return guildEvent.deleteEvent(eventId);
		}
		return false;
	}

	public boolean signUpToEvent(String guildId, String eventId, String role, Member member) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		if (guildEvent != null) {
			return guildEvent.signUpToEvent(eventId, role, member);
		}
		return false;
	}

	public boolean leaveEvent(String guildId, String eventId, String role, Member member) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		if (guildEvent != null) {
			return guildEvent.leave(eventId, role, member);
		}
		return false;
	}

	public String getEventSpecificStringInfo(String guildId, String eventId) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		return guildEvent.getSpecificEventStringInfo(eventId);
	}
	
	public List<Event> cleanEvents(String guildId) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		if (guildEvent != null) {
			return guildEvent.getEventToClean();
		}
		return new ArrayList<>();
	}
	
	public List<Event> getRecurEvents(String guildId) {
		GuildEvent guildEvent = this.guildEvents.get(guildId);
		if (guildEvent != null) {
			return guildEvent.getEventsToRecur();
		}
		return new ArrayList<>();
	}
}
