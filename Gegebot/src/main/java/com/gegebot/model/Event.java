package com.gegebot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import discord4j.core.object.entity.Member;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Event {

	// looks weird, but need info to know which message to clean
	@NonNull
	private String eventId;

	// looks weird, but need info to know which channel to post
	@NonNull
	private String channelId;

	@NonNull
	private EventConfiguration eventConfiguration;

	// role, userList
	private Map<String, List<Member>> joinersMap = new HashMap<>();

	public void signUp(String emojiUnicode, Member member) {

		String role = this.getRoleFromRoleConfiguration(emojiUnicode);

		List<Member> members = this.joinersMap.get(role);
		if (members == null) {
			members = new ArrayList<>();
		}
		members.add(member);
		joinersMap.put(role, members);
	}

	public void leave(String emojiUnicode, Member member) {

		String role = this.getRoleFromRoleConfiguration(emojiUnicode);

		List<Member> members = this.joinersMap.get(role);
		members.remove(member);
		joinersMap.put(role, members);
	}

	public String toString() {
		String resp = "";
		resp += "title: " + this.eventConfiguration.getTitle() + "\n";
		resp += "time: <t:" + this.eventConfiguration.getEventStartEpoch() + ">" + "\n";

		for (Entry<String, RoleConfiguration> entry : this.eventConfiguration.getRoleConfigurationMap().entrySet()) {
			resp += entry.getKey() + " (" + entry.getValue().getCount() + ")" + " " + entry.getValue().getEmojiUnicode()
					+ ": ";
			List<Member> joiners = joinersMap.get(entry.getKey());
			if (joiners != null) {
				resp += "\n";
				for (Member joiner : joiners) {
					resp += "    " + joiner.getDisplayName() + "\n";
				}
			} else {
				resp += "\n";
			}
		}
		return resp;
	}

	private String getRoleFromRoleConfiguration(String emojiUnicode) {
		for (Entry<String, RoleConfiguration> entry : eventConfiguration.getRoleConfigurationMap().entrySet()) {
			if (emojiUnicode.equals(entry.getValue().getEmojiUnicode())) {
				return entry.getKey();
			}
		}
		return null;
	}
}
