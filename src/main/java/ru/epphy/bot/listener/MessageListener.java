package ru.epphy.bot.listener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.epphy.config.ConfigProvider;
import ru.epphy.config.model.GuildSettings;
import ru.epphy.filter.FilterManager;
import ru.epphy.storage.StorageManager;
import ru.epphy.util.LoggerUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public final class MessageListener extends ListenerAdapter implements IListener {
    private static final Executor DELAYED_EXECUTOR = CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS);
    private static final String FAILED_SEND_MESSAGE =
                """
                ❌ Message was removed.
                - Must be a valid, unique link.
                - Allowed domains: %s
                - Message time: `%s`
                """;
    private static final long CACHE_HOLD_TIME = 100L;
    private final Set<String> temporaryCachedLinks = new HashSet<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        LoggerUtil.debug(this, "Receive");
        handleIncomingMessage(event.getMessage(), event.getGuild(), event.getChannel(), event.getMember());
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        LoggerUtil.debug(this, "Update");
        handleIncomingMessage(event.getMessage(), event.getGuild(), event.getChannel(), event.getMember());
    }

    private void handleIncomingMessage(Message message, Guild guild, MessageChannel channel, Member member) {

        if (member == null || guild == null || channel == null || message == null) {
            LoggerUtil.warn(this, "Missing required data: member/guild/channel/message is null.");
            return;
        }

        final String guildId = guild.getId();
        final String channelId = channel.getId();
        final String content = message.getContentRaw();

        LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Validation triggered: \"%s\""
                .formatted(guildId, channelId, member.getUser().getId(), content));

        final GuildSettings settings = ConfigProvider.getInstance().getRegistry().getGuildSettings(guildId);
        if (!shouldValidate(settings, channelId, member, content)) {
            LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Message bypassed validation."
                    .formatted(guildId, channelId, member.getUser().getId()));
            return;
        }

        final boolean validated = FilterManager.getInstance()
                .validateContent(guildId, channelId, member.getUser().getId(), content);

        if (validated) {
            LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Passed validation."
                    .formatted(guildId, channelId, member.getUser().getId()));
            StorageManager.getInstance().addNewLink(guildId, channelId, content);
        } else {
            handleInvalidMessage(message, channel, settings);
        }
    }

    private void handleInvalidMessage(Message message, MessageChannel channel, GuildSettings settings) {
        final String content = message.getContentRaw();

        LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Failed validation: \"%s\""
                .formatted(settings.getGuildId(), message.getChannelId(), message.getAuthor().getId(), content));

        message.delete().queue();

        channel.sendMessage(FAILED_SEND_MESSAGE.formatted(settings.getAllowedDomainsOf(message.getChannelId()), "not completed"))
                .queue(botMessage -> CompletableFuture.runAsync(() -> botMessage.delete().queue(), DELAYED_EXECUTOR));
    }

    // Reserved for future smarter invalid message responses
    @SuppressWarnings("unused")
    private void handleMessageWithResponse() {
        throw new UnsupportedOperationException("Custom response handling not implemented yet.");
    }

    private boolean shouldValidate(GuildSettings guildSettings, String channelId, Member member, String content) {
        if (!guildSettings.isChannelFiltered(channelId))
            return false;

        if (isCached(content))
            return false;

        if (member.getUser().isBot())
            return false;

        if (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR))
            return false;

        final Set<String> modRoles = guildSettings.getModeratorRoles();
        return member.getRoles().stream().noneMatch(role -> modRoles.contains(role.getId()));
    }

    private boolean isCached(@NotNull String link) {
        if (temporaryCachedLinks.contains(link)) return true;

        temporaryCachedLinks.add(link);
        CompletableFuture.delayedExecutor(CACHE_HOLD_TIME, TimeUnit.MILLISECONDS)
                .execute(() -> temporaryCachedLinks.remove(link));
        return false;
    }
}
