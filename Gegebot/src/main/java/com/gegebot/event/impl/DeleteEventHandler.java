package com.gegebot.event.impl;

import com.gegebot.event.MessageCreateHandler;
import com.gegebot.model.EventOrganizer;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class DeleteEventHandler implements MessageCreateHandler {
	private EventOrganizer eventOrganizer;

	public DeleteEventHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	@Override
	public void execute(MessageCreateEvent event) {
		String guildId = event.getMessage().getGuildId().get().asString();
		// parse incoming from discord, remove !delete to get eventId
		String eventId = event.getMessage().getContent().replace("!delete ", "");

		if (this.eventOrganizer.deleteEvent(guildId, eventId))
		{
			// delete message event
			event.getMessage().getChannel().block().getMessageById(Snowflake.of(eventId)).block().delete().block();
			event.getMessage().delete().block();
		}
	}

}
