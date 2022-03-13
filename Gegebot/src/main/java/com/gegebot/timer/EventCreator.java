package com.gegebot.timer;

import java.util.TimerTask;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventCreator extends TimerTask {

	@NonNull
	private GatewayDiscordClient client;
	@NonNull
	private String channelId;
	@NonNull
	private String jsonStr;
	
	@Override
	public void run() {
		TextChannel channel = (TextChannel) this.client.getChannelById(Snowflake.of(this.channelId)).block();
		channel.createMessage("!event " + jsonStr).block();
	}
}
