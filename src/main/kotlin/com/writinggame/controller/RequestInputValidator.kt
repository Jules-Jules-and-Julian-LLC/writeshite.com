package com.writinggame.controller

import com.writinggame.controller.viewModels.ErrorResponse
import com.writinggame.domain.ErrorType
import com.writinggame.model.GameSettings
import java.time.ZonedDateTime

object RequestInputValidator {
    fun validateInput(receivedDateTime: ZonedDateTime, lobbyId: String? = null, username: String? = null,
                      message: String? = null, settings: GameSettings? = null): ErrorResponse? {
        if(lobbyId != null && !isLobbyIdValid(lobbyId)) {
            return ErrorResponse(ErrorType.INVALID_LOBBY_ID, receivedDateTime)
        }
        if(username != null && !isUsernameValid(username)) {
            return ErrorResponse(ErrorType.INVALID_USERNAME, receivedDateTime)
        }
        if(message != null && settings != null && !isMessageValid(message, settings)) {
            return ErrorResponse(ErrorType.INVALID_MESSAGE, receivedDateTime)
        }

        return null
    }

    private fun isMessageValid(message: String, settings: GameSettings): Boolean {
        val messageWordCount = getWordCount(message)
        return message.isNotEmpty() &&
                (settings.maxWordsPerMessage == null || messageWordCount <= settings.maxWordsPerMessage) &&
                (settings.minWordsPerMessage == null || messageWordCount >= settings.minWordsPerMessage) &&
                message.length <= 30000
    }

    private fun getWordCount(text: String): Int {
        return text.trim().split("\\s+".toRegex()).size
    }

    private fun isLobbyIdValid(lobbyId: String): Boolean {
        return lobbyId.isNotEmpty() && lobbyId.matches("""^[A-Za-z0-9-_]{1,64}${'$'}""".toRegex())
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.isNotEmpty() && username.length <= 64
    }
}