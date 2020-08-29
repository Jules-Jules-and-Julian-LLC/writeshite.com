package com.writinggame.controller.viewModels

import com.writinggame.domain.ResponseType
import java.time.ZonedDateTime

open class Response(val responseType: ResponseType, val eventReceivedDatetime: ZonedDateTime) {
}