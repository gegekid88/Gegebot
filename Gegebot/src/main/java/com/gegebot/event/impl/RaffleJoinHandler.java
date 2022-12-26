package com.gegebot.event.impl;

import com.gegebot.event.EventJoinHandler;
import com.gegebot.model.Raffle;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;

public class RaffleJoinHandler extends EventJoinHandler {

	private Raffle raffle;

	public RaffleJoinHandler(Raffle raffle) {
		this.raffle = raffle;
	}

	@Override
	public void process(ReactionAddEvent event) {
		// ignore other message reacts
		if (!raffle.messageIdExists(event.getMessageId().asString()))
			return;

		Member member = event.getMember().orElse(null);

		raffle.joinRaffle(member);
	}
}
