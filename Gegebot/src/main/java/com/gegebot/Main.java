package com.gegebot;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gegebot.event.MessageCreateHandler;
import com.gegebot.event.impl.CreateEventHandler;
import com.gegebot.event.impl.CreateRaffleEventHandler;
import com.gegebot.event.impl.DeleteEventHandler;
import com.gegebot.event.impl.DrawRaffleHandler;
import com.gegebot.event.impl.JoinHandler;
import com.gegebot.event.impl.NotJoinHandler;
import com.gegebot.event.impl.PingHandler;
import com.gegebot.event.impl.RaffleJoinHandler;
import com.gegebot.model.EventOrganizer;
import com.gegebot.model.Raffle;
import com.gegebot.timer.EventCleaner;
import com.gegebot.util.ConfigurationLoader;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import reactor.core.publisher.Mono;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	// eventOrganizer
	private static final EventOrganizer eventOrganizer = new EventOrganizer();
	
	// raffle
	private static final Raffle raffle = new Raffle();

	// discord events
	private static final Map<String, MessageCreateHandler> messageCreateHandlers = new HashMap<>();
	private static final JoinHandler joinHandler = new JoinHandler(eventOrganizer);
	private static final NotJoinHandler notJoinHandler = new NotJoinHandler(eventOrganizer);
	private static final RaffleJoinHandler raffleJoinHandler = new RaffleJoinHandler(raffle);
	static {
		messageCreateHandlers.put("ping", new PingHandler());
		messageCreateHandlers.put("event", new CreateEventHandler(eventOrganizer));
		messageCreateHandlers.put("delete", new DeleteEventHandler(eventOrganizer));
		messageCreateHandlers.put("raffle", new CreateRaffleEventHandler(raffle));
		messageCreateHandlers.put("drawWinner", new DrawRaffleHandler(raffle));
	}

	public static void main(String[] args) {

		// load config
		ConfigurationLoader.loadConfig();

		// Creates the gateway client and connects to the gateway
		final GatewayDiscordClient client = DiscordClientBuilder.create(ConfigurationLoader.BOT_TOKEN).build().login()
				.block();
		// ! commands
		client.getEventDispatcher().on(MessageCreateEvent.class).onErrorResume(mono -> Mono.empty())
				.subscribe(event -> {
					final String content = event.getMessage().getContent();
					for (final Map.Entry<String, MessageCreateHandler> entry : messageCreateHandlers.entrySet()) {
						if (content.startsWith('!' + entry.getKey())) {
							LOGGER.info("execute " + entry.getKey());
							entry.getValue().execute(event);
							break;
						}
					}
				});

		// sign up
		client.getEventDispatcher().on(ReactionAddEvent.class).onErrorResume(mono -> Mono.empty())
			.subscribe(event -> {
				LOGGER.info("execute join");
				joinHandler.execute(event);
				raffleJoinHandler.execute(event);
			});

		// leave
		client.getEventDispatcher().on(ReactionRemoveEvent.class).onErrorResume(mono -> Mono.empty())
				.subscribe(event -> {
					LOGGER.info("execute leave");
					notJoinHandler.execute(event);
				});

		Timer timer = new Timer();
		TimerTask task = new EventCleaner(timer, client, eventOrganizer);
		timer.schedule(task, 0, 60000);

		client.onDisconnect().block();
	}
}