package com.gegebot.event.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gegebot.event.EventJoinHandler;
import com.gegebot.model.EventOrganizer;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.MessageEditSpec;

// sign up to event
// emojiUnicode
public class JoinHandler extends EventJoinHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JoinHandler.class);
	private EventOrganizer eventOrganizer;

	public JoinHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	@Override
	public void process(ReactionAddEvent event) {
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
			LOGGER.info(member.getUsername() + " successfully joined " + messageId);
		}
	}
}
