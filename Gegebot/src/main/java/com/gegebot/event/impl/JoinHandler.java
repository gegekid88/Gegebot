package com.gegebot.event.impl;

import com.gegebot.model.EventOrganizer;
import com.gegebot.util.ConfigurationLoader;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.MessageEditSpec;

// sign up to event
// emojiUnicode
public class JoinHandler {

	private EventOrganizer eventOrganizer;

	public JoinHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	public void execute(ReactionAddEvent event) {

		// ignore myself
		if (event.getUserId().asString().equals(ConfigurationLoader.BOT_ID))
			return;
		
		String messageId = event.getMessageId().asString();
		
		// to improve
		boolean isUnicodeEmoji = event.getEmoji().asUnicodeEmoji().isPresent();
		if (!isUnicodeEmoji) return;
		
		String emojiStr = event.getEmoji().asUnicodeEmoji().get().getRaw();
		
		Member member = event.getMember().orElse(null);

		if (this.eventOrganizer.signUpToEvent(messageId, emojiStr, member)) {
			String msg = "----------------------------------------------------------\n";
			msg += this.eventOrganizer.getEventSpecificStringInfo(messageId);
			msg += "----------------------------------------------------------";

			event.getMessage().block().edit(MessageEditSpec.builder().contentOrNull(msg).build()).block();
		}
	}
}
