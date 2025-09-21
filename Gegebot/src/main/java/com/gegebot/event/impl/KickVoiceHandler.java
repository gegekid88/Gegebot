package com.gegebot.event.impl;

import com.gegebot.event.MessageCreateHandler;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.PartialMember;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class KickVoiceHandler implements MessageCreateHandler {
	
	@Override
	public void execute(MessageCreateEvent event) {
		event.getMessage().getChannel()
			.flatMap(channel -> {
				if (!event.getMessage().getMemberMentions().isEmpty()) {
					PartialMember mentionedPartialMember = event.getMessage().getMemberMentions().get(0);

					// Resolve the PartialMember to a full Member
					return mentionedPartialMember.asFullMember()
						.flatMap(memberToKick -> {
							// Send an initial confirmation message
							return channel.createMessage(String.format("Okay, %s will be disconnected from the voice channel in 10 minutes.", memberToKick.getDisplayName()))
								.then(disconnectMemberAfterDelay(memberToKick, channel));
						});
				}
				
				return Mono.empty();
			})
			.subscribe();
	}
	
	private Mono<Void> disconnectMemberAfterDelay(Member memberToKick, discord4j.core.object.entity.channel.MessageChannel channel) {
		return memberToKick.getVoiceState()
			.flatMap(voiceState -> {
				// Proceed only if the member is still in a voice channel
				if (voiceState.getChannelId().isPresent()) {
					// Use Mono.delay to wait for 10 minutes
					return Mono.delay(Duration.ofMinutes(10))
						.flatMap(ignored -> {
							// Check again if the member is still in a voice channel after the delay
							return memberToKick.getVoiceState()
								.flatMap(updatedVoiceState -> {
									if (updatedVoiceState.getChannelId().isPresent()) {
										return memberToKick.edit(spec -> spec.setNewVoiceChannel(null))
											.then(channel.createMessage(String.format("Disconnected %s from their voice channel.", memberToKick.getDisplayName())))
											.then();
									} else {
										return channel.createMessage(String.format("%s left the voice channel during the waiting period.", memberToKick.getDisplayName()))
											.then();
									}
								});
						});
				} else {
					return channel.createMessage(String.format("%s is not in a voice channel.", memberToKick.getDisplayName()))
						.then();
				}
			})
			.onErrorResume(e -> channel.createMessage("Failed to kick member. Ensure the bot has the 'Move Members' permission.").then());
	}
}