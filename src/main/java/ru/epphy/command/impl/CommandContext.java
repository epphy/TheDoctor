package ru.epphy.command.impl;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

    public String getGuildId() {
        return guild.getId();
    }

    public String getChannelId() {
        return channel.getId();
    }

    public String getMemberId() {
        return member.getId();
    }

    public String getUserId() {
        return user.getId();
    }

    public void reply(String content, boolean ephemeral) {
        event.reply(content).setEphemeral(ephemeral).queue();
    }
}
