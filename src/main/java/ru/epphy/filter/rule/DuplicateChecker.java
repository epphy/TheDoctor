package ru.epphy.filter.rule;

import org.jetbrains.annotations.NotNull;

public final class DuplicateChecker implements IRule {

    @Override
    public boolean validate(@NotNull String guildId, @NotNull String content) {
        throw new UnsupportedOperationException("Not ready yet");
    }

    @Override
    public Response getResponse() {
        return null;
    }

}
