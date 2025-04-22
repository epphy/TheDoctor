package ru.epphy.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class LoggerUtil {
    private static final String LOG_TEMPLATE = "[Guild: %-18s] [Channel: %-18s] [User: %-18s] Content: %-50s | %s";

    public void debug(@NotNull Object source, @NotNull String message) {
        getLogger(getSender(source)).debug(message);
    }

    public void debug(@NotNull Object source, String guildId, String channelId, String userId, String content, @NotNull String message) {
        getLogger(getSender(source)).debug(LOG_TEMPLATE.formatted(guildId, channelId, userId, content, message));
    }

    public void info(@NotNull Object source, @NotNull String message) {
        getLogger(getSender(source)).info(message);
    }

    public void warn(@NotNull Object source, @NotNull String message) {
        getLogger(getSender(source)).warn(message);
    }

    public void warn(@NotNull Object source, String guildId, String channelId, String userId, String content, @NotNull String message) {
        getLogger(getSender(source)).warn(LOG_TEMPLATE.formatted(guildId, channelId, userId, content, message));
    }

    public void error(@NotNull Object source, @NotNull String message) {
        getLogger(getSender(source)).error(message);
    }

    public void error(@NotNull Object source, @NotNull String message, @NotNull Throwable t) {
        getLogger(getSender(source)).error(message, t);
    }

    private Logger getLogger(Class<?> source) {
        return LoggerFactory.getLogger(source);
    }

    @NotNull
    private Class<?> getSender(Object o) {
        return o.getClass();
    }
}
