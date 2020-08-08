package me.alex.obama.listeners

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.function.Consumer

private const val DEFAULT_DESCRIPTION = "This command requires a description. Bully Fern or Alex if you want to them to change it.";

data class BotCommand(
        val name: String,
        val event: (MessageReceivedEvent) -> Unit,
        val description: String = DEFAULT_DESCRIPTION
) {

    // Java constructor
    constructor(name: String, event: Consumer<MessageReceivedEvent>) : this(name = name, event = {e -> event.accept(e)}, description = DEFAULT_DESCRIPTION)

    // Java constructor
    constructor(name: String, event: Consumer<MessageReceivedEvent>, description: String) : this(name = name, event = {e -> event.accept(e)}, description = description)
}