package ru.epphy.config.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import ru.epphy.util.LoggerUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class GuildSettingsRegistry implements IConfig {

    private static final String FILE_NAME = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final TypeToken<Map<String, GuildSettings>> TYPE_TOKEN = new TypeToken<>(){};
    private final Map<String, GuildSettings> guildSettings = new HashMap<>();
    private File file;

    @Override
    public void init() {
        loadFile();
        reload();
    }

    private void loadFile() {
        if (file == null) {
            file = new File(FILE_NAME);
        }

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    LoggerUtil.info(this, "Created new default config file");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create a default config file", e);
            }
        }
    }

    @Override
    public void reload() {
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final Map<String, GuildSettings> tempGuildSettings = GSON.fromJson(reader, TYPE_TOKEN);
            if (tempGuildSettings == null) {
                LoggerUtil.warn(this, "Guild settings occurred to be null at reading");
                return;
            }
            guildSettings.clear();
            guildSettings.putAll(tempGuildSettings);
        } catch (IOException e) {
            throw new RuntimeException("Could not read config file", e);
        }
    }

    @Override
    public void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(GSON.toJson(guildSettings));
        } catch (IOException e) {
            throw new RuntimeException("Could not write to config file", e);
        }
    }

    @NotNull
    public GuildSettings getGuildSettings(@NotNull String guildId) {
        final boolean absent = !hasGuildSettings(guildId);
        if (absent) LoggerUtil.debug(this, "Guild settings of '%s' are not present. Creating a default".formatted(guildId));
        return guildSettings.computeIfAbsent(guildId, _ -> new GuildSettings(guildId));
    }

    public boolean hasGuildSettings(@NotNull String guildId) {
        return guildSettings.containsKey(guildId);
    }
}
