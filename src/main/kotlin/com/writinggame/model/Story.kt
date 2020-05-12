package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

class Story(@JsonIgnore val creatorSessionId: String) {
    val messages: MutableList<Message> = mutableListOf()
    val storyId: String = UUID.randomUUID().toString()

    fun addMessage(message: String, creatorSessionId: String) {
        messages.add(Message(message, creatorSessionId))
    }
}
