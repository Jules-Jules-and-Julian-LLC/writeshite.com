package com.writinggame.model

import java.util.*

class Story(val creatingPlayer: Player) {
    val messages: MutableList<Message> = mutableListOf()
    val id: String = UUID.randomUUID().toString()

    fun addMessage(text: String, creatorSessionId: String) {
        if(text.isNotEmpty()) {
            messages.add(Message(text, creatorSessionId))
        }
    }
}
