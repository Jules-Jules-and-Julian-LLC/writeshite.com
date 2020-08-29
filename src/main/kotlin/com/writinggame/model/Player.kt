package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

//TODO player text color, error if username empty
data class Player(@JsonIgnore val clientId: String, var username: String, var waitingSince: ZonedDateTime? = null)