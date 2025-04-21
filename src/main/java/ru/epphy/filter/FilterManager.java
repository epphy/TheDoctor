package ru.epphy.filter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.epphy.filter.rule.*;
import ru.epphy.util.LoggerUtil;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilterManager {
    private static FilterManager instance;
    private final Set<IRule> rules = new HashSet<>();

    public static FilterManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new FilterManager();
        }

        instance.loadFilters();
        LoggerUtil.info(instance, "initialized");
    }

    private void loadFilters() {
        rules.clear();
        rules.add(new LinkChecker());
        rules.add(new DomainChecker());
        rules.add(new DuplicateChecker());
    }

    public boolean validateContent(@NotNull String guildId, @NotNull String channelId, @NotNull String userId, @NotNull String content) {
        for (final IRule rule : rules) {
            if (!rule.validate(guildId, content)) {
                LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Content: [ %s ] did not pass the check of %s"
                        .formatted(guildId, channelId, userId, content, rule.getClass().getSimpleName()));
                return false;
            }
        }

        LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Content: [ %s ] passed the check"
                .formatted(guildId, channelId, userId, content));
        return true;
    }

    public Response validateContentWithResponse(@NotNull String guildId, @NotNull String channelId, @NotNull String userId, @NotNull String content) {
        for (final IRule rule : rules) {
            if (!rule.validate(guildId, content)) {
                LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Content: [ %s ] did not pass the check of %s"
                        .formatted(guildId, channelId, userId, content, rule.getClass().getSimpleName()));
                return rule.getResponse();
            }
        }

        LoggerUtil.debug(this, "[Guild: %s] [Channel: %s] [User: %s] Content: [ %s ] passed the check"
                .formatted(guildId, channelId, userId, content));
        return Response.GOOD;
    }
}
