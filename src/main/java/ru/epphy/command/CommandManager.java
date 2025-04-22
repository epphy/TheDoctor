package ru.epphy.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.epphy.command.impl.CommandContext;
import ru.epphy.command.impl.ConfigCommand;
import ru.epphy.command.impl.ICommand;
import ru.epphy.config.ConfigProvider;
import ru.epphy.config.model.GuildSettings;
import ru.epphy.util.LoggerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandManager {
    private static CommandManager instance;
    private final Map<String, ICommand> commands = new HashMap<>();

    public static CommandManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    public static void init(JDA jda) {
        if (instance == null) {
            instance = new CommandManager();
        }

        instance.registerCommands(jda);
        LoggerUtil.info(instance, "initialized");
    }

    private void registerCommands(JDA jda) {
        final ConfigCommand configCommand = new ConfigCommand();

        final SlashCommandData configSlashCommand = Commands.slash("config", "Manage guild configuration")
                .addOption(STRING, "key", "The config section (allowed-domains, moderator-roles, filtered-channels)", true, true)
                .addOption(STRING, "action", "The action to perform (show, add, remove)", true, true)
                .addOption(STRING, "value", "Value to insert (not needed for show)", false, true);

        jda.updateCommands()
                .addCommands(configSlashCommand)
                .queue(success -> LoggerUtil.info(this, "Slash commands registered."),
                        error -> LoggerUtil.error(this, "Failed to register slash commands", error));

        commands.put("config", configCommand);
    }

    public static void unload() {
        instance = null;
    }

    public void dispatchCommand(SlashCommandInteractionEvent event) {
        if (!hasPermission(event.getGuild().getId(), event.getMember())) return;

        final String command = event.getName();
        final ICommand commandExecutor = commands.get(command);

        if (commandExecutor == null) {
            handleInvalidUsage(event);
            return;
        }

        final String key = getOptionValue(event, "key");
        final String action = getOptionValue(event, "action");
        final String value = getOptionValue(event, "value");

        if (key == null || action == null) {
            handleInvalidUsage(event);
            return;
        }

        // Build an args list depending on presence
        final List<String> args = value != null
                ? List.of(key, action, value)
                : List.of(key, action);



        final CommandContext commandContext = new CommandContext(
                event.getGuild().getId(),
                event.getChannelId(),
                event.getUser().getId(),
                args,
                event.getUser(),
                event.getMember(),
                event.getGuild(),
                event
        );

        commandExecutor.execute(commandContext);
    }

    public void dispatchCommandTabComplete(CommandAutoCompleteInteractionEvent event) {

    }

    private void routeCommand() {

    }

    private void executeCommand(ICommand command, Guild guild, MessageChannel channel, Member member, List<String> args) {
        final var commandContext = new CommandContext(
                guild.getId(),
                channel.getId(),
                member.getUser().getId(),
                args,
                member.getUser(),
                member,
                guild,

        );

        command.execute(commandContext);
    }

    private boolean hasPermission(String guildId, Member member) {
        final GuildSettings guildSettings = ConfigProvider.getInstance().getRegistry().getGuildSettings(guildId);
        final Set<String> moderatorIdRoles = guildSettings.getModeratorRoles();

        if (member.isOwner() || member.getPermissions().contains(Permission.ADMINISTRATOR))
            return true;

        for (final Role role : member.getRoles()) {
            if (moderatorIdRoles.contains(role.getId())) return true;
        }

        return false;
    }

    private String getOptionValue(SlashCommandInteractionEvent event, String name) {
        final OptionMapping option = event.getOption(name);
        return option != null ? option.getAsString() : null;
    }

    private void handleInvalidUsage(SlashCommandInteractionEvent event) {
        event.reply("Invalid command").setEphemeral(true).queue();
    }
}
