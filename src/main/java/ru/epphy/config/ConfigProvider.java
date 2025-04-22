package ru.epphy.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.epphy.config.model.GuildSettingsRegistry;
import ru.epphy.util.LoggerUtil;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigProvider {
    private static ConfigProvider instance;
    private GuildSettingsRegistry registry;

    public static ConfigProvider getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new ConfigProvider();
        }

        instance.loadConfigs();
        LoggerUtil.info(instance, "initialized");
    }

    private void loadConfigs() {
        registry = new GuildSettingsRegistry();
        registry.init();
    }

    public void reload() {
        loadConfigs();
    }

    public static void unload() {
        instance = null;
    }
}
