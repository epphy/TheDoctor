package ru.epphy.filter.rule;

import org.jetbrains.annotations.NotNull;
import ru.epphy.config.ConfigProvider;
import ru.epphy.config.model.GuildSettings;

public final class DomainChecker implements IRule {

    @Override
    public boolean validate(@NotNull String guildId, @NotNull String channelId, @NotNull String content) {
        final GuildSettings guildSettings = ConfigProvider.getInstance().getRegistry().getGuildSettings(guildId);
        for (final String allowedDomain : guildSettings.getAllowedDomainsOf(channelId)) {
            if (content.contains(allowedDomain)) return true;
        }
        return false;
    }

    @Override
    public Response getResponse() {
        return Response.NOT_ALLOWED_DOMAIN;
    }

}
