package ru.epphy.filter.rule;

import org.jetbrains.annotations.NotNull;

public interface IRule {
    boolean validate(@NotNull String guildId, @NotNull String channelId, @NotNull String content);
    Response getResponse();
}
