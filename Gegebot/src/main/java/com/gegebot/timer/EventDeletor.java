package com.gegebot.timer;

import java.util.TimerTask;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventDeletor extends TimerTask {

	@NonNull
	private GatewayDiscordClient client;
	@NonNull
	private String channelId;
	@NonNull
	private String eventId;

	@Override
	public void run() {
		this.client.getMessageById(Snowflake.of(this.channelId), Snowflake.of(this.eventId)).block().delete().block();
	}
}
