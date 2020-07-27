package me.alex.obama.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BotConfigData implements Serializable {

    private String token = "FIXME";

    private Map<Long, ServerSettings> guildSettingsMap = new HashMap<>();


    public Map<Long, ServerSettings> getGuildSettingsMap() {
        return guildSettingsMap;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotConfigData)) return false;
        BotConfigData that = (BotConfigData) o;
        return token.equals(that.token) &&
                guildSettingsMap.equals(that.guildSettingsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, guildSettingsMap);
    }
}
