package com.gegebot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import discord4j.core.object.entity.Member;

//todo null pattern instead of null checking
public class EventOrganizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventOrganizer.class);
	private Map<String, Event> events = new HashMap<>();

	public void createEvent(EventConfiguration eventConfiguration) {
		Event event = new Event(eventConfiguration);
		events.put(eventConfiguration.getEventId(), event);
	}

	public Event deleteEvent(String eventId) {
		return events.remove(eventId);
	}

	public boolean signUpToEvent(String eventId, String role, Member member) {
		Event event = this.events.get(eventId);
		if (event != null) {
			event.signUp(role, member);
			return true;
		}
		return false;
	}

	public boolean leaveEvent(String eventId, String role, Member member) {
		Event event = this.events.get(eventId);
		if (event != null) {
			event.leave(role, member);
			return true;
		}
		return false;
	}

	public String getEventSpecificStringInfo(String eventId) {
		Event event = this.events.get(eventId);
		return event.toString();
	}

	public List<Event> getEndingEvents() {
		List<Event> events = new ArrayList<>();
		Iterator<Entry<String, Event>> iterator = this.events.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Event> entry = iterator.next();
		    Event event = entry.getValue();
		    
		    // if event past currentTime
		    if (System.currentTimeMillis() / 1000 >= event.getEventConfiguration().getEventStartEpoch()) {
		    	LOGGER.info("end: " + entry.getKey());
		    	events.add(entry.getValue());
		    	iterator.remove();
			}
		}
		return events;
	}
}
