package com.gegebot.event.impl;

import com.gegebot.event.MessageCreateHandler;
import com.gegebot.model.Raffle;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;

public class CreateRaffleEventHandler implements MessageCreateHandler {
	private Raffle raffle;

	public CreateRaffleEventHandler(Raffle raffle) {
		this.raffle = raffle;
	}

	public void execute(MessageCreateEvent event) {
		try {
			String message = "Join gege sugar raffle!";

			Message msg = ((TextChannel) event.getMessage().getChannel().block()).createMessage(message).block();

			msg.addReaction(ReactionEmoji.unicode("\ud83d\ude08")).block();

			raffle.addMessage(msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
