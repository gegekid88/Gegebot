package com.gegebot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import discord4j.core.object.entity.Member;

public class GuildEvent {

	private Map<String, Event> events = new HashMap<>();

	public void createEvent(String channelId, String eventId, EventConfiguration eventConfiguration) {
		Event event = new Event(channelId, eventConfiguration);
		events.put(eventId, event);
	}

	public String getSpecificEventStringInfo(String eventId) {
		return this.events.get(eventId).toString();
	}

	public void signUpToEvent(String eventId, String role, Member member) {
		if (this.events.get(eventId) != null) {
			Event event = this.events.get(eventId);
			event.signUp(role, member);
		}
	}

	public void leave(String eventId, String role, Member member) {
		if (this.events.get(eventId) != null) {
			Event event = this.events.get(eventId);
			event.leave(role, member);
		}
	}

	public List<Event> getEventsToRecur() {
		List<Event> recurEvents = new ArrayList<>();
		for (Map.Entry<String, Event> entry : this.events.entrySet()) {
			Event e = entry.getValue();

			// if recur is set and current event is pass current time, recreate event
			if (e.getEventConfiguration().getRecurTimeInSeconds() > 0
					&& System.currentTimeMillis() / 1000 > e.getEventConfiguration().getEventStartEpoch()) {
				recurEvents.add(entry.getValue());
			}
		}
		return recurEvents;
	}

	public void cleanEvents() {
		Iterator<Entry<String, Event>> iterator = this.events.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Event> entry = iterator.next();
		    Event event = entry.getValue();
		    
		    // if event is pass current time, clean it
		    if (System.currentTimeMillis() / 1000 > event.getEventConfiguration().getEventStartEpoch()) {
		    	iterator.remove();
			}
		}
	}
}
