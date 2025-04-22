package ru.epphy.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import ru.epphy.command.impl.CommandContext;
import ru.epphy.command.impl.ConfigCommand;
import ru.epphy.command.impl.ICommand;
import ru.epphy.config.ConfigProvider;
import ru.epphy.util.LoggerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandManager {
    private static CommandManager instance;
    private final Map<String, ICommand> commands = new HashMap<>();

    public static CommandManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        return instance;
    }

    public static void init(@NotNull JDA jda) {
        if (instance == null) {
            instance = new CommandManager();
        }
        instance.registerCommands(jda);
        LoggerUtil.info(instance, "initialized");
    }

    public static void unload() {
        instance = null;
    }

    private void registerCommands(JDA jda) {
        register(new ConfigCommand());

        jda.updateCommands()
                .addCommands(commands.values().stream()
                        .map(ICommand::getSlashCommandData)
                        .toList())
                .queue(
                        success -> LoggerUtil.info(this, "Slash commands registered"),
                        error -> LoggerUtil.error(this, "Failed to register slash commands", error)
                );
    }

    private void register(@NotNull ICommand command) {
        commands.put(command.getName(), command);
    }

    public void dispatchCommand(@NotNull SlashCommandInteractionEvent event) {
        final ICommand command = commands.get(event.getName());

        if (command == null) {
            handleInvalidUsage(event, "Unknown command.");
            return;
        }

        final Guild guild = event.getGuild();
        final Member member = event.getMember();

        if (guild == null || member == null) {
            handleInvalidUsage(event, "Guild or member is null.");
            return;
        }

        if (!hasPermission(guild.getId(), member)) {
            handleInvalidUsage(event, "Insufficient permissions.");
            return;
        }

        final CommandContext context = buildContext(event, guild, member);
        command.execute(context);
    }

    public void dispatchCommandAutoComplete(@NotNull CommandAutoCompleteInteractionEvent event) {
        final ICommand command = commands.get(event.getName());
        if (command != null) {
            command.handleAutoComplete(event);
        } else {
            event.replyChoices(List.of()).queue();
        }
    }

    @NotNull
    private CommandContext buildContext(SlashCommandInteractionEvent event, Guild guild, Member member) {
        return new CommandContext(
                guild,
                event.getChannel(),
                member,
                member.getUser(),
                event.getOptions().stream().map(OptionMapping::getAsString).toList(),
                event
        );
    }

    private void handleInvalidUsage(@NotNull SlashCommandInteractionEvent event, String reason) {
        event.reply("❌ Invalid command or insufficient permissions.").setEphemeral(true).queue();
        LoggerUtil.warn(this, "Command '%s' failed: %s".formatted(event.getName(), reason));
    }

    private boolean hasPermission(String guildId, Member member) {
        if (member.isOwner() || member.getPermissions().contains(Permission.ADMINISTRATOR)) return true;

        final Set<String> moderatorIdRoles = ConfigProvider.getInstance().getRegistry()
                .getGuildSettings(guildId)
                .getModeratorRoles();

        return member.getRoles().stream()
                .map(Role::getId)
                .anyMatch(moderatorIdRoles::contains);
    }
}
