package ru.epphy.filter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.epphy.util.LoggerUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Todo: Load filtered channels from configs
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChannelFilterManager {
    private static ChannelFilterManager instance;
    private final Map<String, Set<String>> filteredChannelsByGuild = new ConcurrentHashMap<>();

    public static ChannelFilterManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new ChannelFilterManager();
        }
        LoggerUtil.info(instance, "initialized");
    }

    public boolean addChannelFor(@NotNull String guildId, @NotNull String channelId) {
        ensureGuildPresence(guildId);
        final boolean added = filteredChannelsByGuild.get(guildId).add(channelId);
        LoggerUtil.debug(this, "Added channel '%s' for guild '%s': %s".formatted(channelId, guildId, added));
        return added;
    }

    public boolean removeChannelFor(@NotNull String guildId, @NotNull String channelId) {
        ensureGuildPresence(guildId);
        final boolean removed = filteredChannelsByGuild.get(guildId).remove(channelId);
        LoggerUtil.debug(this, "Removed channel '%s' for guild '%s': %s".formatted(channelId, guildId, removed));
        return removed;
    }

    public boolean isChannelFilteredOf(@NotNull String guildId, @NotNull String channelId) {
        ensureGuildPresence(guildId);
        return filteredChannelsByGuild.get(guildId).contains(channelId);
    }

    public boolean addGuild(@NotNull String guildId) {
        final boolean present = isGuildPresent(guildId);
        ensureGuildPresence(guildId);
        LoggerUtil.debug(this, "Added guild '%s' to map: %s".formatted(guildId, !present));
        return !present;
    }

    public boolean removeGuild(@NotNull String guildId) {
        final boolean present = isGuildPresent(guildId);
        filteredChannelsByGuild.remove(guildId);
        LoggerUtil.debug(this, "Removed guild '%s' from map: %s".formatted(guildId, present));
        return present;
    }

    public boolean isGuildPresent(@NotNull String guildId) {
        return filteredChannelsByGuild.containsKey(guildId);
    }

    @NotNull
    public Set<String> getFilteredChannelsOf(@NotNull String guildId) {
        ensureGuildPresence(guildId);
        return Set.copyOf(filteredChannelsByGuild.get(guildId));
    }

    private void ensureGuildPresence(@NotNull String guildId) {
        filteredChannelsByGuild.computeIfAbsent(guildId, _ -> new HashSet<>());
        LoggerUtil.debug(this, "Guild '%s' was not found in map. Creating a default entry for it".formatted(guildId));
    }

    private Map<String, Set<String>> dumpState() {
        return Map.copyOf(filteredChannelsByGuild);
    }
}
