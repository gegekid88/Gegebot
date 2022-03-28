package com.gegebot.event.impl;

import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gegebot.event.MessageCreateHandler;
import com.gegebot.model.EventConfiguration;
import com.gegebot.model.EventOrganizer;
import com.gegebot.model.RoleConfiguration;
import com.vdurmont.emoji.EmojiParser;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageEditSpec;

// create event
public class CreateEventHandler implements MessageCreateHandler {

	private EventOrganizer eventOrganizer;

	public CreateEventHandler(EventOrganizer eventOrganizer) {
		this.eventOrganizer = eventOrganizer;
	}

	@Override
	public void execute(MessageCreateEvent event) {
		try {
			// parse incoming from discord, emojis to unicode, remove !event from message
			String jsonStr = EmojiParser.parseToUnicode(event.getMessage().getContent().replace("!event ", ""));

			// json to eventConfiguration object
			EventConfiguration eventConfiguration = new ObjectMapper().readValue(jsonStr, EventConfiguration.class);

			// derive channelId and store so future won't derive repeatedly
			String scheduledChannelId = event.getGuild().block().getChannels()
					.filter(channel -> channel.getName().equals(eventConfiguration.getScheduledChannelName())).blockFirst().getId().asString();
			eventConfiguration.setScheduledChannelId(scheduledChannelId);
			String partyMeetingChannelId = event.getGuild().block().getChannels()
					.filter(channel -> channel.getName().equals(eventConfiguration.getPartyMeetingChannelName())).blockFirst().getId().asString();
			eventConfiguration.setPartyMeetingChannelId(partyMeetingChannelId);
			
			// message creation
			Message msg = ((TextChannel) event.getGuild().block().getChannelById(Snowflake.of(scheduledChannelId)).block())
					.createMessage("-").block();
			eventConfiguration.setEventId(msg.getId().asString()); // store msgId as eventId
			for (Entry<String, RoleConfiguration> entry : eventConfiguration.getRoleConfigurationMap().entrySet()) {
				msg.addReaction(ReactionEmoji.unicode(entry.getValue().getEmojiUnicode())).block();
			}
			msg.edit(MessageEditSpec.builder().contentOrNull(eventConfiguration.toString()).build()).block();

			// setup event
			this.eventOrganizer.createEvent(eventConfiguration);

			// clean !event message
			event.getMessage().delete().block();
		} catch (Exception e) {
			e.printStackTrace();
			event.getMessage().getChannel().block().createMessage("Invalid.").block();
		}
	}
}
