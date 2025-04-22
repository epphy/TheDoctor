package ru.epphy.command.impl;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public record CommandContext(
        Guild guild,
        MessageChannel channel,
        Member member,
        User user,
        List<String> args,
        SlashCommandInteractionEvent event
) {

    public void reply(String content, boolean ephemeral) {
        event.reply(content).setEphemeral(ephemeral).queue();
    }
}
