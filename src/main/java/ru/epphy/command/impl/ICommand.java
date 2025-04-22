package ru.epphy.command.impl;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICommand {
    String getName();
    SlashCommandData getSlashCommandData();
    void execute(CommandContext context);
    default void handleAutoComplete(@NotNull CommandAutoCompleteInteractionEvent event) {
        event.replyChoices(List.of()).queue();
    }
}
