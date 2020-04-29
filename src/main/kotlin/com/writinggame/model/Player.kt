package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore

//TODO player text color, error if username empty
data class Player(@JsonIgnore val clientId: String, var username: String)