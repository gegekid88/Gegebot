package com.gegebot.event.impl;

import com.gegebot.event.MessageCreateHandler;
import com.gegebot.model.Raffle;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;

public class DrawRaffleHandler implements MessageCreateHandler {
	private Raffle raffle;
	
	public DrawRaffleHandler(Raffle raffle) {
		this.raffle = raffle;
	}

	@Override
	public void execute(MessageCreateEvent event) {
		Member member = raffle.drawRaffle();
		String messageStr = "Congrats " + member.getTag() + "! Your rng is 1/" 
				+ raffle.getRaffleJoinersCount() + ".";
		
		for (Message message: raffle.getMessages()) {
			(message.getChannel().block()).createMessage(messageStr).block();
		}
	}
}
