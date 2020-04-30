package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore

//TODO player text color, error if username empty
data class Player(val clientId: String, var username: String)