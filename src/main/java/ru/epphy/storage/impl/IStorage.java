package ru.epphy.storage.impl;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IStorage {
    boolean add(@NotNull String guildId, @NotNull String channelId, @NotNull String link);
    boolean remove(@NotNull String guildId, @NotNull String channelId, @NotNull String link);
    boolean contains(@NotNull String guildId, @NotNull String channelId, @NotNull String link);
    Set<String> getSentMessagesInChannelOfGuild(@NotNull String guildId, @NotNull String channelId);
}
