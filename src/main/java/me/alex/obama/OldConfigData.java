package me.alex.obama;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OldConfigData {

    private String token = "FIXME";

    private Map<Long, List<Long>> botSpamChannels = new HashMap<>();

    public String getToken() {
        return token;
    }

    public Map<Long, List<Long>> getBotSpamChannels() {
        return botSpamChannels;
    }

}
