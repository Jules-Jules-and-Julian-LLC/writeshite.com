package com.writinggame.controller

import com.writinggame.controller.viewModels.ErrorResponse
import com.writinggame.domain.ErrorType
import java.time.ZonedDateTime

object RequestInputValidator {
    fun validateInput(receivedDateTime: ZonedDateTime, lobbyId: String? = null, username: String? = null): ErrorResponse? {
        if(lobbyId != null && !isLobbyIdValid(lobbyId)) {
            return ErrorResponse(ErrorType.INVALID_LOBBY_ID, receivedDateTime)
        }
        if(username != null && !isUsernameValid(username)) {
            return ErrorResponse(ErrorType.INVALID_USERNAME, receivedDateTime)
        }

        return null
    }

    private fun isLobbyIdValid(lobbyId: String): Boolean {
        return lobbyId.isNotEmpty() && lobbyId.matches("""^[A-Za-z0-9-_]{1,64}${'$'}""".toRegex())
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.isNotEmpty() && username.length <= 64
    }
}