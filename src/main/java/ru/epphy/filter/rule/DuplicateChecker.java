package ru.epphy.filter.rule;

import org.jetbrains.annotations.NotNull;
import ru.epphy.storage.StorageManager;

public final class DuplicateChecker implements IRule {

    @Override
    public boolean validate(@NotNull String guildId, @NotNull String channelId, @NotNull String content) {
        return !StorageManager.getInstance().isDuplicate(guildId, channelId, content);
    }

    @Override
    public Response getResponse() {
        return Response.DUPLICATE;
    }

}
