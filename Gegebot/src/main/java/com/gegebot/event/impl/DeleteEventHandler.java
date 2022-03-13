package com.gegebot.event.impl;

import com.gegebot.event.MessageCreateHandler;
import com.gegebot.model.Event;
import com.gegebot.model.EventOrganizer;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;

public class DeleteEventHandler implements MessageCreateHandler {
	private EventOrganizer eventOrganizer;

	public DeleteEventHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	@Override
	public void execute(MessageCreateEvent event) {
		// parse incoming from discord, remove !delete to get eventId
		String eventId = event.getMessage().getContent().replace("!delete ", "");

		Event deletedEvent = this.eventOrganizer.deleteEvent(eventId);

		if (deletedEvent != null) {
			String channelId = deletedEvent.getEventConfiguration().getScheduledChannelId();
			// delete message event
			((TextChannel) event.getGuild().block().getChannelById(Snowflake.of(channelId)).block())
					.getMessageById(Snowflake.of(eventId)).block().delete().block();
			event.getMessage().delete().block();
		}
	}

}
