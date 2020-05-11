package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore

class Story(@JsonIgnore val creatorSessionId: String) {
    val messages: MutableList<Message> = mutableListOf()
}
