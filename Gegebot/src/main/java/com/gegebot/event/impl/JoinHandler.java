package com.gegebot.event.impl;

import com.gegebot.model.EventOrganizer;
import com.gegebot.util.ConfigurationLoader;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.MessageEditSpec;

// sign up to event
// emojiUnicode
public class JoinHandler{
	
	private EventOrganizer eventOrganizer;

	public JoinHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	public void execute(ReactionAddEvent event) {
		
		// ignore myself
		if (event.getUserId().asString().equals(ConfigurationLoader.BOT_ID)) return;

		String messageId = event.getMessageId().asString();
		String guildId = event.getGuildId().get().asString();
		String emojiStr = event.getEmoji().asUnicodeEmoji().orElse(null).getRaw();
		Member member = event.getMember().orElse(null);

		this.eventOrganizer.signUpToEvent(guildId, messageId, emojiStr, member);
		
		String msg = "----------------------------------------------------------\n";
		msg += this.eventOrganizer.getEventSpecificStringInfo(guildId, messageId);
		msg += "----------------------------------------------------------";
		
		event.getMessage().block().edit(MessageEditSpec.builder().contentOrNull(msg).build()).block();
	}
}
