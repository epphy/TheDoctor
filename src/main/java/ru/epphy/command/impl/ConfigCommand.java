package ru.epphy.command.impl;

import org.jetbrains.annotations.NotNull;
import ru.epphy.config.ConfigProvider;
import ru.epphy.config.model.GuildSettings;

public final class ConfigCommand implements ICommand {

    @Override
    public void execute(@NotNull CommandContext commandContext) {
        // <parameter> <action> [arg] -- expected form
        final int argsSize = commandContext.args().size();
        if (argsSize < 2 || argsSize > 3) {
            handleInvalidUsage(commandContext);
            return;
        }

        final String key = commandContext.args().getFirst().toLowerCase().trim();
        final String action = commandContext.args().get(1).toLowerCase().trim();
        final String value = argsSize == 3 ? commandContext.args().getLast().toLowerCase().trim() : null;

        routeCommand(commandContext, key, action, value);
    }

    private void routeCommand(CommandContext ctx, String key, String action, String value) {
        switch (key) {
            case "filtered-channels" -> handleChannels(ctx, action, value);
            case "moderator-roles" -> handleModRoles(ctx, action, value);
            case "allowed-domains" -> handleDomains(ctx, action, value);
            default -> handleInvalidUsage(ctx);
        }
    }

    private void handleChannels(CommandContext commandContext, String action, String value) {
        final GuildSettings config = ConfigProvider.getInstance().getRegistry().getGuildSettings(commandContext.guildId());

        switch (action) {
            case "add" -> {
                config.addFilteredChannel(value);
                commandContext.reply("Added channel: %s".formatted(value), true);
            }

            case "remove" -> {
                config.removeFilteredChannel(value);
                commandContext.reply("Removed channel: %s".formatted(value), true);
            }

            case "show" -> commandContext.reply("Filtered channels: %s".formatted(config.getFilteredChannels()), true);

            default -> handleInvalidUsage(commandContext);
        }
    }

    private void handleModRoles(CommandContext commandContext, String action, String value) {
        final GuildSettings config = ConfigProvider.getInstance().getRegistry().getGuildSettings(commandContext.guildId());

        switch (action) {
            case "add" -> {
                config.addModRole(value);
                commandContext.reply("Added mod role: %s".formatted(value), true);
            }

            case "remove" -> {
                config.removeModRole(value);
                commandContext.reply("Removed mod role: %s".formatted(value), true);
            }

            case "show" -> commandContext.reply("Mod roles: %s".formatted(config.getModeratorRoles()), true);

            default -> handleInvalidUsage(commandContext);
        }
    }

    private void handleDomains(CommandContext commandContext, String action, String value) {
        final GuildSettings config = ConfigProvider.getInstance().getRegistry().getGuildSettings(commandContext.guildId());

        switch (action) {
            case "add" -> {
                config.addAllowedDomain(commandContext.channelId(), value);
                commandContext.reply("Added domain: %s".formatted(value), true);
            }

            case "remove" -> {
                config.removeAllowedDomain(commandContext.channelId(), value);
                commandContext.reply("Removed domain: %s".formatted(value), true);
            }

            case "show" -> commandContext.reply("Filtered channels: %s".formatted(config.getPrettyAllowedDomainsOf(commandContext.channelId())), true);

            default -> handleInvalidUsage(commandContext);
        }
    }

    private void handleInvalidUsage(CommandContext commandContext) {
        commandContext.reply("Invalid command", true);
    }
}
