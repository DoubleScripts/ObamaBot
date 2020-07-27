package me.alex.obama

import com.github.fernthedev.config.common.Config
import com.github.fernthedev.config.common.exceptions.ConfigLoadException
import com.github.fernthedev.fernutils.thread.ThreadUtils
import me.alex.obama.config.ChannelList
import me.alex.obama.config.ServerSettings
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import java.util.*
import kotlin.collections.ArrayList

/**
 * @param guild the guild handled
 * @param long the channel id
 * @param boolean if the channel was added, true, else removed
 *
 */
typealias EventListener = (Long, Long, Boolean) -> Unit

class ConfigManager<T : Config<BotConfigData>>(private val config: T) {


    private val eventMap: MutableMap<ChannelList, MutableList<EventListener>> = EnumMap(ChannelList::class.java)

    private fun getEventListeners(channelList: ChannelList): List<EventListener> {
        return if (eventMap[channelList] == null) emptyList()
        else eventMap[channelList]!!
    }

    fun registerEventListener(channelList: ChannelList, eventListener: EventListener) {
        if (eventMap[channelList] == null) {
            eventMap[channelList] = ArrayList()
        }

        eventMap[channelList]!!.add(eventListener);
    }

    private fun createServerSettings(guildChannel: Long): ServerSettings {
        val botSpamChannels = config.configData.guildSettingsMap

        if (botSpamChannels.containsKey(guildChannel))
            return botSpamChannels[guildChannel]!!

        botSpamChannels[guildChannel] = ServerSettings()

        return botSpamChannels[guildChannel]!!
    }

    protected fun getServerSettings(guildChannel: Guild): ServerSettings? {
        return getServerSettings(guildChannel.idLong)
    }

    protected fun getServerSettings(guildChannel: Long): ServerSettings? {
        val botSpamChannels = config.configData.guildSettingsMap
        return botSpamChannels[guildChannel]
    }

    fun removeGuild(guild: Long) {
        val botSpamChannels = config.configData.guildSettingsMap
        botSpamChannels.remove(guild)
    }

    fun addChannel(guildChannel: GuildChannel, channelLists: ChannelList) {
        addChannel(guildChannel.guild, guildChannel.idLong, channelLists)
    }

    fun addChannel(guild: Guild, guildChannel: Long, channelLists: ChannelList) {
        addChannel(guild.idLong, guildChannel, channelLists)
    }

    protected fun addChannel(guild: Long, guildChannel: Long, channelLists: ChannelList) {
        var serverSettings = getServerSettings(guild)

        if (serverSettings == null) {
            serverSettings = createServerSettings(guild)
        }

        serverSettings.addChannel(channelLists, guildChannel)

        try {
            config.syncSave()
        } catch (e: ConfigLoadException) {
            e.printStackTrace()
        }

        ThreadUtils.runForLoopAsync(getEventListeners(channelLists)) {listener ->

            listener(guild, guildChannel, true)
        }.runThreads(Main.getExecutorService())
    }

    fun  removeChannel(guildChannel: GuildChannel, channelLists: ChannelList) {
        removeChannel(guildChannel.guild, guildChannel.idLong, channelLists)
    }


    fun removeChannel(guild: Guild, guildChannel: Long, channelLists: ChannelList) {
        var serverSettings = getServerSettings(guild)

        if (serverSettings == null) {
            serverSettings = createServerSettings(guild.idLong)
        }

        serverSettings.removeChannel(channelLists, guildChannel)
        if (serverSettings.isEmptyMap) {
            config.configData.guildSettingsMap.remove(guild.idLong)
        }

        try {
            config.syncSave()
        } catch (e: ConfigLoadException) {
            e.printStackTrace()
        }

        ThreadUtils.runForLoopAsync(getEventListeners(channelLists)) {listener ->
            listener(guild.idLong, guildChannel, false)
        }.runThreads(Main.getExecutorService())
    }

    fun <C : GuildChannel> isChannelInRegistry(channel: C, channelList: ChannelList?): Boolean {
        return (getServerSettings(channel.guild) != null
                && getServerSettings(channel.guild)!!.getChannels(channelList).contains(channel.idLong))
    }

    val configData: BotConfigData
        get() = config.configData

    val serverList: MutableMap<Long, ServerSettings>
        get() = config.configData.guildSettingsMap

    @Throws(ConfigLoadException::class)
    fun load() {
        config.load()
    }

    @Throws(ConfigLoadException::class)
    fun syncLoad() {
        config.syncLoad()
    }

    @Throws(ConfigLoadException::class)
    fun save() {
        config.save()
    }

    @Throws(ConfigLoadException::class)
    fun syncSave() {
        config.syncSave()
    }



}