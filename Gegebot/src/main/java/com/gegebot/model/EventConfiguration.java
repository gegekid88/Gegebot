package com.gegebot.model;

import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventConfiguration {
	
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("scheduledChannelName")
	private String scheduledChannelName;
	
	@JsonProperty("partyMeetingChannelName")
	private String partyMeetingChannelName;

	@JsonProperty("eventStartEpoch")
	private Integer eventStartEpoch;

	// 0 if no recur, todo optional
	@JsonProperty("recurTimeInSeconds")
	private Integer recurTimeInSeconds;
	
	@JsonProperty("roles")
	private Map<String, RoleConfiguration> roleConfigurationMap;
	
	// hacks
	private String scheduledChannelId;
	private String partyMeetingChannelId;
	private String eventId;
	
	public String toString() {
		String msg = "----------------------------------------------------------\n";
		msg += "eventId: " + this.eventId + "\n";
		msg += "title: " + this.title + "\n";
		msg += "time: <t:" + this.eventStartEpoch + ">" + "\n";
		for (Entry<String, RoleConfiguration> entry : this.roleConfigurationMap.entrySet()) {
			msg += entry.getKey() + " (" + entry.getValue().getCount() + ")" + " " + entry.getValue().getEmojiUnicode()
					+ ":\n";
		}
		msg += "----------------------------------------------------------";
		return msg;
	}
}

