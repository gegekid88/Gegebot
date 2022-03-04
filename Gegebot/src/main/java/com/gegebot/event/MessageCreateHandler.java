package com.gegebot.event;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface MessageCreateHandler {

    void execute(MessageCreateEvent event);
}