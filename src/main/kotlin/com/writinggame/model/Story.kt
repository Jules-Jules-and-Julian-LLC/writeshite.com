package com.writinggame.model

import java.util.*

class Story(val creatingPlayer: Player) {
    val messages: MutableList<Message> = mutableListOf()
    val id: String = UUID.randomUUID().toString()

    fun addMessage(text: String, creatorSessionId: String, settings: GameSettings) {
        val message = Message(text.trim(), creatorSessionId)
        if((settings.minWordsPerMessage == null || settings.minWordsPerMessage <= message.wordCount())
            && (settings.maxWordsPerMessage == null || message.wordCount() <= settings.maxWordsPerMessage)) {
            messages.add(message)
        }
    }
}
