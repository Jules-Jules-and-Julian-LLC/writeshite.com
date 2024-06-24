package com.writinggame.controller.viewModels

import com.writinggame.domain.ErrorType
import com.writinggame.domain.ResponseType
import java.time.ZonedDateTime

class ErrorResponse(val errorType: ErrorType, receivedDatetime: ZonedDateTime) : Response(ResponseType.ERROR, receivedDatetime)