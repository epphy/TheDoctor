package ru.epphy.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.epphy.storage.impl.IStorage;
import ru.epphy.storage.impl.InMemoryStorage;
import ru.epphy.util.LoggerUtil;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StorageManager {
    private static StorageManager instance;
    private IStorage storage;

    public static StorageManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new StorageManager();
        }

        instance.storage = new InMemoryStorage(); // Todo: Temp storage.
        LoggerUtil.info(instance, "initialized");
    }

    public static void unload() {
        instance = null;
    }

    public boolean addNewLink(@NotNull String guildId, @NotNull String channelId, @NotNull String link) {
        return storage.add(guildId, channelId, link);
    }

    public boolean removeLink(@NotNull String guildId, @NotNull String channelId, @NotNull String link) {
        return storage.remove(guildId, channelId, link);
    }

    public boolean isDuplicate(@NotNull String guildId, @NotNull String channelId, @NotNull String link) {
        return storage.contains(guildId, channelId, link);
    }

    public Set<String> getAllSentLinks(@NotNull String guildId, @NotNull String channelId) {
        return storage.getSentMessagesInChannelOfGuild(guildId, channelId);
    }
}
