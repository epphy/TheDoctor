package ru.epphy.bot.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class MessageListener extends ListenerAdapter implements IListener {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        handleMessage();
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        handleMessage();
    }

    private void handleMessage() {

    }
}
