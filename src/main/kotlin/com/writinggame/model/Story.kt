package com.writinggame.model

import java.util.*

class Story(val creatingPlayer: Player) {
    val messages: MutableList<Message> = mutableListOf()
    val id: String = UUID.randomUUID().toString()

    fun addMessage(message: String, creatorSessionId: String) {
        messages.add(Message(message, creatorSessionId))
    }
}
