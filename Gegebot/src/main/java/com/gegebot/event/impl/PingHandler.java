package com.gegebot.event.impl;

import com.gegebot.event.MessageCreateHandler;

import discord4j.core.event.domain.message.MessageCreateEvent;

// ping pong to check if bot is alive
public class PingHandler implements MessageCreateHandler {
	
	@Override
	public void execute(MessageCreateEvent event) {
		event.getMessage().getChannel().block().createMessage("Pong!").block();
	}
}