package com.writinggame.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

data class Player(@JsonIgnore val clientId: String, var username: String, @JsonIgnore var waitingSince: ZonedDateTime? = null)