package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Message(val text: String, @JsonIgnore val creatorSessionId: String)
