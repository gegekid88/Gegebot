package com.gegebot.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;

public class Raffle {
	private static final Logger LOGGER = LoggerFactory.getLogger(Raffle.class);
	
	private Map<String, Message> messages = new HashMap<>();
	private Map<String, Member> raffleJoiners = new HashMap<>();

	public void joinRaffle(Member member) {
		raffleJoiners.put(member.getId().asString(), member);
		LOGGER.debug(member.getUsername() + " joined raffle.");
	}

	public Member drawRaffle() {
		List<Object> ids = Arrays.asList(raffleJoiners.keySet().toArray());
		LOGGER.debug("Total raffle joiners: " + ids.size());
		
		Random rand = new Random();
		int randNumber = rand.nextInt(ids.size());
		Member winner = raffleJoiners.get((String)ids.get(randNumber));
		return winner;
	}
	
	public int getRaffleJoinersCount() {
		return this.raffleJoiners.size();
	}
	
	public void addMessage(Message message) {
		messages.put(message.getId().asString(), message);
	}
	
	public boolean messageIdExists(String messageId) {
		return messages.containsKey(messageId);
	}
	
	public Collection<Message> getMessages() {
		return messages.values();
	}
}
