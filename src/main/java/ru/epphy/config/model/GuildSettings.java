package ru.epphy.config.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.epphy.config.ConfigProvider;
import ru.epphy.util.LoggerUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class GuildSettings {
    private final String guildId;
    private final Map<String, Set<String>> allowedDomainsByChannel = new HashMap<>();
    private final Set<String> filteredChannels = new HashSet<>();
    private final Set<String> moderatorRoles = new HashSet<>();

    // Todo we update config every request due to config save skill issue
    /* === ALLOWED DOMAINS === */

    public boolean addAllowedDomain(@NotNull String channelId, @NotNull String domain) {
        final boolean added = allowedDomainsByChannel
                .computeIfAbsent(channelId, _ -> new HashSet<>())
                .add(domain);

        if (!added) {
            LoggerUtil.warn(this, "Allowed domain '%s' already exists for channel '%s' in guild '%s'"
                    .formatted(domain, channelId, guildId));
            return false;
        }

        ConfigProvider.getInstance().getRegistry().save();
        return true;
    }

    public boolean removeAllowedDomain(@NotNull String channelId, @NotNull String domain) {
        final boolean removed = allowedDomainsByChannel
                .computeIfAbsent(channelId, _ -> new HashSet<>())
                .remove(domain);

        if (!removed) {
            LoggerUtil.warn(this, "Allowed domain '%s' not found in channel '%s' for guild '%s'"
                    .formatted(domain, channelId, guildId));
            return false;
        }

        ConfigProvider.getInstance().getRegistry().save();
        return true;
    }

    public boolean isAllowedDomainIn(@NotNull String channelId, @NotNull String domain) {
        return allowedDomainsByChannel
                .computeIfAbsent(channelId, _ -> new HashSet<>())
                .contains(domain);
    }

    public Set<String> getAllowedDomainsOf(@NotNull String channelId) {
        return Set.copyOf(allowedDomainsByChannel.computeIfAbsent(channelId, _ -> new HashSet<>()));
    }

    public String getPrettyAllowedDomainsOf(@NotNull String channelId) {
        return String.join(", ", getAllowedDomainsOf(channelId));
    }

    /* === FILTERED CHANNELS === */

    public boolean addFilteredChannel(@NotNull String channelId) {
        final boolean added = filteredChannels.add(channelId);

        if (!added) {
            LoggerUtil.info(this, "Channel '%s' is already in filtered channels for guild '%s'"
                    .formatted(channelId, guildId));
            return false;
        }

        ConfigProvider.getInstance().getRegistry().save();
        return true;
    }

    public boolean removeFilteredChannel(@NotNull String channelId) {
        final boolean removed = filteredChannels.remove(channelId);

        if (!removed) {
            LoggerUtil.warn(this, "Channel '%s' not found in filtered channels for guild '%s'"
                    .formatted(channelId, guildId));
            return false;
        }

        ConfigProvider.getInstance().getRegistry().save();
        return true;
    }

    public boolean removeAllFilteredChannels() {
        if (filteredChannels.isEmpty()) {
            LoggerUtil.warn(this, "Guild '%s' has no filtered channels".formatted(guildId));
            return false;
        }

        filteredChannels.clear();
        ConfigProvider.getInstance().getRegistry().save();
        LoggerUtil.info(this, "All filtered channels cleared for guild '%s'".formatted(guildId));
        return true;
    }

    public boolean isChannelFiltered(@NotNull String channelId) {
        return filteredChannels.contains(channelId);
    }

    public Set<String> getFilteredChannels() {
        return Set.copyOf(filteredChannels);
    }

    /*

    MODERATOR ROLES

     */

    /* === MODERATOR ROLES === */

    public boolean addModRole(@NotNull String roleId) {
        final boolean added = moderatorRoles.add(roleId);

        if (!added) {
            LoggerUtil.info(this, "Role '%s' is already a moderator role for guild '%s'"
                    .formatted(roleId, guildId));
            return false;
        }

        ConfigProvider.getInstance().getRegistry().save();
        return true;
    }

    public boolean removeModRole(@NotNull String roleId) {
        final boolean removed = moderatorRoles.remove(roleId);

        if (!removed) {
            LoggerUtil.warn(this, "Role '%s' is not a moderator role in guild '%s'"
                    .formatted(roleId, guildId));
            return false;
        }

        ConfigProvider.getInstance().getRegistry().save();
        return true;
    }

    public boolean isMod(@NotNull String roleId) {
        return moderatorRoles.contains(roleId);
    }
}
