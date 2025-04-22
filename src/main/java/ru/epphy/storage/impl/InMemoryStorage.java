package ru.epphy.storage.impl;

import org.jetbrains.annotations.NotNull;
import ru.epphy.util.LoggerUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryStorage implements IStorage {
    private final Map<String, Map<String, Set<String>>> sentLinksInChannelsByGuild = new ConcurrentHashMap<>();

    @Override
    public boolean add(@NotNull String guildId, @NotNull String channelId, @NotNull String link) {
        ensurePresenceOfChannel(guildId, channelId);
        final boolean added = sentLinksInChannelsByGuild.get(guildId).get(channelId).add(link);
        LoggerUtil.debug(this, "Added '%s' to channel '%s' of guild '%s': %s".formatted(link, channelId, guildId, added));
        return added;
    }

    @Override
    public boolean remove(@NotNull String guildId, @NotNull String channelId, @NotNull String link) {
        ensurePresenceOfChannel(guildId, channelId);
        final boolean removed = sentLinksInChannelsByGuild.get(guildId).get(channelId).remove(link);
        LoggerUtil.debug(this, "Removed '%s' from channel '%s' of guild '%s': %s".formatted(link, channelId, guildId, removed));
        return removed;
    }

    @Override
    public boolean contains(@NotNull String guildId, @NotNull String channelId, @NotNull String link) {
        ensurePresenceOfChannel(guildId, channelId);
        return sentLinksInChannelsByGuild.get(guildId).get(channelId).contains(link);
    }

    @Override
    public Set<String> getSentMessagesInChannelOfGuild(@NotNull String guildId, @NotNull String channelId) {
        ensurePresenceOfChannel(guildId, channelId);
        return Set.copyOf(sentLinksInChannelsByGuild.get(guildId).get(channelId));
    }

    public void clearChannel(@NotNull String guildId, @NotNull String channelId) {
        ensurePresenceOfChannel(guildId, channelId);
        sentLinksInChannelsByGuild.get(guildId).get(channelId).clear();
    }

    public void clearGuild(@NotNull String guildId) {
        ensurePresenceOfGuild(guildId);
        sentLinksInChannelsByGuild.get(guildId).clear();
    }

    public void clear() {
        sentLinksInChannelsByGuild.clear();
    }

    private void ensurePresenceOfChannel(@NotNull String guildId, @NotNull String channelId) {
        ensurePresenceOfGuild(guildId);
        sentLinksInChannelsByGuild.get(guildId).computeIfAbsent(channelId, id -> new HashSet<>());
    }

    private void ensurePresenceOfGuild(@NotNull String guildId) {
        sentLinksInChannelsByGuild.computeIfAbsent(guildId, id -> new ConcurrentHashMap<>());
    }
}
