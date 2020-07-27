package me.alex.obama.config;

import net.dv8tion.jda.api.entities.GuildChannel;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.map.UnmodifiableMap;

import java.io.Serializable;
import java.util.*;

public class ServerSettings implements Serializable {

    private Map<ChannelList, List<Long>> channelListsListMap = new HashMap<>();

    public int listSizeMap() {
        return channelListsListMap.size();
    }

    public boolean isEmptyMap() {
        return channelListsListMap.isEmpty();
    }

    public List<Long> getChannels(ChannelList channelLists) {

        List<Long> channels = channelListsListMap.get(channelLists);

        if (channels == null) channels = Collections.emptyList();

        return new UnmodifiableList<>(channels);
    }

    public Map<ChannelList, List<Long>> unmodifiableChannelListsListMap() {
        return UnmodifiableMap.unmodifiableMap(channelListsListMap);
    }

    public void addChannel(ChannelList channelLists, GuildChannel channel) {
        addChannel(channelLists, channel.getIdLong());
    }

    public void addChannel(ChannelList channelLists, long channel) {
        if (!channelListsListMap.containsKey(channelLists)) {
            channelListsListMap.put(channelLists, new ArrayList<>());
        }

        channelListsListMap.get(channelLists).add(channel);
    }

    public void removeChannel(ChannelList channelLists, GuildChannel channel) {
        removeChannel(channelLists, channel.getIdLong());
    }

    public void removeChannel(ChannelList channelLists, long channel) {
        if (!channelListsListMap.containsKey(channelLists))
            return;

        channelListsListMap.get(channelLists).remove(channel);

        if (channelListsListMap.containsKey(channelLists) && channelListsListMap.get(channelLists).isEmpty()) {
            channelListsListMap.remove(channelLists);
        }
    }
}
