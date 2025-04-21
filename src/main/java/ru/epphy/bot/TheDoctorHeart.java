package ru.epphy.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import ru.epphy.bot.listener.CommandListener;
import ru.epphy.bot.listener.MessageListener;
import ru.epphy.filter.ChannelFilterManager;
import ru.epphy.filter.FilterManager;
import ru.epphy.util.LoggerUtil;

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

        loadFilters();
        registerListeners();
        LoggerUtil.info(this, "Heartbeat has started");
    }

    private void loadFilters() {
        ChannelFilterManager.init();
        FilterManager.init();
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
        LoggerUtil.info(this, "Heartbeat has stopped");
    }
}
