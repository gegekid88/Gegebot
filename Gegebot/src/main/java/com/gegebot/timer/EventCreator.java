package com.gegebot.timer;

import java.util.TimerTask;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;

public class EventCreator extends TimerTask{

	private GatewayDiscordClient client;
	private String channelId;
	private String jsonStr;
	
	public EventCreator(GatewayDiscordClient client, String channelId, String jsonStr) {
		this.client = client;
		this.channelId = channelId;
		this.jsonStr = jsonStr;
	}
	
	@Override
	public void run() {
		TextChannel channel = (TextChannel) this.client.getChannelById(Snowflake.of(this.channelId)).block();
		channel.createMessage("!event " + jsonStr).block();
	}
}
