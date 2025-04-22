package ru.epphy.command.impl;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import ru.epphy.config.ConfigProvider;
import ru.epphy.config.model.GuildSettings;

import java.util.List;
import java.util.stream.Stream;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public final class ConfigCommand implements ICommand {

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("config", "Manage guild configuration")
                .addOption(STRING, "key", "Section: allowed-domains, mod-roles, and filtered-channels", true, true)
                .addOption(STRING, "action", "Action: show, add, remove", true, true)
                .addOption(STRING, "value", "Value to add/remove", false, true)
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        final List<String> args = context.args();

        if (args.size() < 2 || args.size() > 3) {
            replyInvalidUsage(context);
            return;
        }

        final String key = args.getFirst().toLowerCase().trim();
        final String action = args.get(1).toLowerCase().trim();
        final String value = args.size() == 3 ? context.args().getLast().toLowerCase().trim() : null;

        routeCommand(context, key, action, value);
    }

    private void routeCommand(CommandContext ctx, String key, String action, String value) {
        switch (key) {
            case "filtered-channels" -> handleFilteredChannels(ctx, action, value);
            case "mod-roles" -> handleModRoles(ctx, action, value);
            case "allowed-domains" -> handleAllowedDomains(ctx, action, value);
            default -> replyInvalidUsage(ctx);
        }
    }

    private void handleFilteredChannels(CommandContext context, String action, String value) {
        final GuildSettings config = ConfigProvider.getInstance().getRegistry().getGuildSettings(context.getGuildId());

        switch (action) {
            case "add" -> {
                config.addFilteredChannel(value);
                context.reply("Added filtered channel: %s".formatted(value), true);
            }

            case "remove" -> {
                config.removeFilteredChannel(value);
                context.reply("Removed filtered channel: %s".formatted(value), true);
            }

            case "show" -> context.reply("Filtered channels: %s".formatted(config.getFilteredChannels()), true);

            default -> replyInvalidUsage(context);
        }
    }

    private void handleModRoles(CommandContext context, String action, String value) {
        final GuildSettings config = ConfigProvider.getInstance().getRegistry().getGuildSettings(context.getGuildId());

        switch (action) {
            case "add" -> {
                config.addModRole(value);
                context.reply("Added mod role: %s".formatted(value), true);
            }

            case "remove" -> {
                config.removeModRole(value);
                context.reply("Removed mod role: %s".formatted(value), true);
            }

            case "show" -> context.reply("Mod roles: %s".formatted(config.getModeratorRoles()), true);

            default -> replyInvalidUsage(context);
        }
    }

    private void handleAllowedDomains(CommandContext context, String action, String value) {
        final GuildSettings config = ConfigProvider.getInstance().getRegistry().getGuildSettings(context.getGuildId());

        switch (action) {
            case "add" -> {
                config.addAllowedDomain(context.getChannelId(), value);
                context.reply("Added domain: %s".formatted(value), true);
            }

            case "remove" -> {
                config.removeAllowedDomain(context.getChannelId(), value);
                context.reply("Removed domain: %s".formatted(value), true);
            }

            case "show" -> context.reply("Allowed domains: %s".formatted(config.getPrettyAllowedDomainsOf(context.getChannelId())), true);

            default -> replyInvalidUsage(context);
        }
    }

    @Override
    public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
        final String focused = event.getFocusedOption().getName();
        final String value = event.getFocusedOption().getValue();

        final List<Command.Choice> options = switch (focused) {
            case "key" -> autocomplete(value, "allowed-domains", "mod-roles", "filtered-channels");
            case "action" -> autocomplete(value, "show", "add", "remove");
            default -> List.of();
        };

        event.replyChoices(options).queue();
    }

    @NotNull
    private List<Command.Choice> autocomplete(String input, String... values) {
        return Stream.of(values)
                .filter(v -> v.startsWith(input))
                .map(v -> new Command.Choice(v, v))
                .toList();
    }

    private void replyInvalidUsage(@NotNull CommandContext ctx) {
        ctx.reply("❌ Invalid usage. Please check your command format.", true);
    }
}
