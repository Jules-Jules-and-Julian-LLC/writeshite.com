package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.writinggame.domain.StoryStateType
import java.util.*

class Story(val creatingPlayer: Player) {
    val messages: MutableList<Message> = mutableListOf()
    val id: String = UUID.randomUUID().toString()
    val state: StoryStateType = StoryStateType.LIVE

    fun addMessage(message: String, creatorSessionId: String) {
        messages.add(Message(message, creatorSessionId))
    }
}
