package com.gegebot.event.impl;

import java.io.IOException;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gegebot.event.MessageCreateHandler;
import com.gegebot.model.EventConfiguration;
import com.gegebot.model.EventOrganizer;
import com.gegebot.model.RoleConfiguration;
import com.vdurmont.emoji.EmojiParser;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;

// create event
public class CreateEventHandler implements MessageCreateHandler {

	private EventOrganizer eventOrganizer;

	public CreateEventHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	@Override
	public void execute(MessageCreateEvent event) {
		try {
			String guildId = event.getMessage().getGuildId().get().asString();
			String channelId = event.getMessage().getChannelId().asString();

			// parse incoming from discord, emojis to unicode, remove !event from message
			String jsonStr = EmojiParser.parseToUnicode(event.getMessage().getContent().replace("!event ", ""));

			// json to eventConfiguration object
			EventConfiguration eventConfiguration = new ObjectMapper().readValue(jsonStr, EventConfiguration.class);

			// message creation
			Message msg = event.getMessage().getChannel().block().createMessage(eventConfiguration.toString()).block();
			for (Entry<String, RoleConfiguration> entry : eventConfiguration.getRoleConfigurationMap().entrySet()) {
				msg.addReaction(ReactionEmoji.unicode(entry.getValue().getEmojiUnicode())).block();
			}

			// setup event
			this.eventOrganizer.createEvent(guildId, channelId, msg.getId().asString(), eventConfiguration);
			
			// clean !event message
			event.getMessage().delete().block();
		} catch (IOException e) {
			e.printStackTrace();
			event.getMessage().getChannel().block().createMessage("Invalid json.").block();
		}
	}
}
