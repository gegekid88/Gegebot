package com.gegebot.timer;

import java.util.TimerTask;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;

public class EventDeletor extends TimerTask {

	private GatewayDiscordClient client;
	private String channelId;
	private String eventId;

	public EventDeletor(GatewayDiscordClient client, String channelId, String eventId) {
		this.client = client;
		this.channelId = channelId;
		this.eventId = eventId;
	}

	@Override
	public void run() {
		this.client.getMessageById(Snowflake.of(this.channelId), Snowflake.of(this.eventId)).block().delete().block();
	}
}
