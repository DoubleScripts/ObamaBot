package me.alex.obama;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BotConfigData implements Serializable {

    private String token = "FIXME";

    private Map<Long, ServerSettings> guildSettingsMap = new HashMap<>();

    public String getToken() {
        return token;
    }

    public Map<Long, ServerSettings> getGuildSettingsMap() {
        return guildSettingsMap;
    }

}
