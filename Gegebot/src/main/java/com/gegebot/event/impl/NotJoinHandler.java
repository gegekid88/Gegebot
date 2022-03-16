package com.gegebot.event.impl;

import com.gegebot.model.EventOrganizer;
import com.gegebot.util.ConfigurationLoader;

import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.MessageEditSpec;

public class NotJoinHandler {

	private EventOrganizer eventOrganizer;

	public NotJoinHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	public void execute(ReactionRemoveEvent event) {

		try {
			// ignore myself
			if (event.getUserId().asString().equals(ConfigurationLoader.BOT_ID))
				return;

			String messageId = event.getMessageId().asString();
			
			// to improve
			boolean isUnicodeEmoji = event.getEmoji().asUnicodeEmoji().isPresent();
			if (!isUnicodeEmoji) return;
			
			String emojiStr = event.getEmoji().asUnicodeEmoji().get().getRaw();
			
			Member member = event.getGuild().block().getMemberById(event.getUserId()).block();
			if (this.eventOrganizer.leaveEvent(messageId, emojiStr, member)) {
				String msg = "----------------------------------------------------------\n";
				msg += this.eventOrganizer.getEventSpecificStringInfo(messageId);
				msg += "----------------------------------------------------------";

				event.getMessage().block().edit(MessageEditSpec.builder().contentOrNull(msg).build()).block();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
