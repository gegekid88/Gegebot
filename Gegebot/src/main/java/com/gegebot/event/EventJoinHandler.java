package com.gegebot.event;

import com.gegebot.util.ConfigurationLoader;

import discord4j.core.event.domain.message.ReactionAddEvent;

public abstract class EventJoinHandler {
	public final void execute(ReactionAddEvent event) {
		try {
			if (event.getUserId().asString().equals(ConfigurationLoader.BOT_ID))
				return;
			
			process(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void process(ReactionAddEvent event);
}
