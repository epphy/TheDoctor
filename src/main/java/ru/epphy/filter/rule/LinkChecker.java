package ru.epphy.filter.rule;

import org.jetbrains.annotations.NotNull;

public final class LinkChecker implements IRule {

    @Override
    public boolean validate(@NotNull String guildId, @NotNull String channelId, @NotNull String content) {
        return content.contains("https") || content.contains("http");
    }

    @Override
    public Response getResponse() {
        return Response.NOT_LINK;
    }

}
