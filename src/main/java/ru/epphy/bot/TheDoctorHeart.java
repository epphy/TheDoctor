package ru.epphy.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import ru.epphy.bot.listener.CommandListener;
import ru.epphy.bot.listener.MessageListener;
import ru.epphy.command.CommandManager;
import ru.epphy.config.ConfigProvider;
import ru.epphy.filter.FilterManager;
import ru.epphy.storage.StorageManager;
import ru.epphy.util.LoggerUtil;

/*
Todo
PLAN:
1. Present the work
2. Find a way to shut down the bot gracefully
3. Use persistent container for duplicates rather than in-memory map
4. Add setting of duration for how long messages can be kept
5. Visualization graphs
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TheDoctorHeart {

    private static TheDoctorHeart instance;
    private String token;
    private JDA jda;

    public static TheDoctorHeart getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        return instance;
    }

    public static void init(@NotNull String token) {
        if (instance == null) {
            instance = new TheDoctorHeart();
        }
        instance.token = token;
    }

    public static void unload() {
        instance = null;
    }

    public void startHeartbeat() {
        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();

        loadConfig();
        loadStorage();
        loadFilters();
        loadCommand();
        registerListeners();
        LoggerUtil.info(this, "Heartbeat has started");
    }

    private void loadConfig() {
        ConfigProvider.init();
    }

    private void loadStorage() {
        StorageManager.init();
    }

    private void loadFilters() {
        FilterManager.init();
    }

    private void loadCommand() {
        CommandManager.init(jda);
    }

    private void registerListeners() {
        final var messageListener = new MessageListener();
        jda.addEventListener(messageListener);

        final var commandListener = new CommandListener();
        jda.addEventListener(commandListener);
    }

    public void restartHeartbeat() {
        LoggerUtil.info(this, "Heartbeat has restarted");
    }

    public void stopHeartbeat() {
        jda.shutdown();
        unloadCommand();
        unloadFilters();
        unloadStorage();
        unloadConfig();
        LoggerUtil.info(this, "Heartbeat has stopped");
    }

    private void unloadCommand() {
        CommandManager.unload();
    }

    private void unloadFilters() {
        FilterManager.unload();
    }

    private void unloadStorage() {
        StorageManager.unload();
    }

    private void unloadConfig() {
        ConfigProvider.unload();
    }
}
