//package com.writinggame
//
//import com.writinggame.controller.RequestInputValidator
//import com.writinggame.controller.viewModels.ErrorResponse
//import com.writinggame.domain.ErrorType
//import com.writinggame.model.GameSettings
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import java.time.ZonedDateTime
//
//class RequestInputValidatorTests {
//    private val now: ZonedDateTime = ZonedDateTime.now()
//
//    @Test
//    fun lobbyIdValid() {
//        assertNull(RequestInputValidator.validateInput(now, "123"))
//        assertNull(RequestInputValidator.validateInput(now, "as-1323__--123oiqweuASDZXCdd"))
//        //32 chars is max
//        assertNull(RequestInputValidator.validateInput(now, "asdf123asdf123asdf123asdf123asdd"))
//
//        //33 characters is invalid
//        assertErrorWithType(ErrorType.INVALID_LOBBY_ID, RequestInputValidator.validateInput(now, "asdf123asdf123asdf123asdf123asdd2"))
//        //empty is invalid
//        assertErrorWithType(ErrorType.INVALID_LOBBY_ID, RequestInputValidator.validateInput(now, ""))
//        //non-alphanumeric is invalid
//        assertErrorWithType(ErrorType.INVALID_LOBBY_ID, RequestInputValidator.validateInput(now, "!"))
//    }
//
//    @Test
//    fun usernameValid() {
//        assertNull(RequestInputValidator.validateInput(now, username = "123"))
//        assertNull(RequestInputValidator.validateInput(now, username = "as-1323__--123oiqweuASDZX"))
//        //32 chars is max
//        assertNull(RequestInputValidator.validateInput(now, username = "asdf123asdf123asdf123asdf123asdd"))
//
//        //33 characters is invalid
//        assertErrorWithType(ErrorType.INVALID_USERNAME, RequestInputValidator.validateInput(now, username = "asdf123asdf123asdf123asdf123asdd2"))
//        assertErrorWithType(ErrorType.INVALID_USERNAME, RequestInputValidator.validateInput(now, username = "really long and obnoxious name asdf areally long and obnoxious name asdf a2"))
//        //empty is invalid
//        assertErrorWithType(ErrorType.INVALID_USERNAME, RequestInputValidator.validateInput(now, username = ""))
//    }
//
//    @Test
//    fun messageValid() {
//        val longMessage = "a".repeat(30000)
//        val tooLongMessage = "a".repeat(30001)
//
//        //No min or max, just need to be non-empty, not super long
//        assertNull(RequestInputValidator.validateInput(now, message = "123", settings = GameSettings(minWordsPerMessage = null)))
//        assertNull(RequestInputValidator.validateInput(now, message = longMessage, settings = GameSettings(minWordsPerMessage = null)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = tooLongMessage, settings = GameSettings(minWordsPerMessage = null)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = "", settings = GameSettings(minWordsPerMessage = null)))
//
//        //Min, no max
//        assertNull(RequestInputValidator.validateInput(now, message = "123 adsf", settings = GameSettings(minWordsPerMessage = 2)))
//        assertNull(RequestInputValidator.validateInput(now, message = "123 adsf asdasd jjasd jja sdj123 12 3jjkl123j lkasdn,m jk123kjh ", settings = GameSettings(minWordsPerMessage = 2)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = "123", settings = GameSettings(minWordsPerMessage = 2)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = "", settings = GameSettings(minWordsPerMessage = 2)))
//
//        //Max, no min
//        assertNull(RequestInputValidator.validateInput(now, message = "123", settings = GameSettings(minWordsPerMessage = null, maxWordsPerMessage = 2)))
//        assertNull(RequestInputValidator.validateInput(now, message = " 123 ", settings = GameSettings(minWordsPerMessage = null, maxWordsPerMessage = 2)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = "123 123 123 123", settings = GameSettings(minWordsPerMessage = null, maxWordsPerMessage = 2)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = "", settings = GameSettings(minWordsPerMessage = null, maxWordsPerMessage = 2)))
//
//        //Min and max
//        assertNull(RequestInputValidator.validateInput(now, message = "123 123", settings = GameSettings(minWordsPerMessage = 2, maxWordsPerMessage = 5)))
//        assertNull(RequestInputValidator.validateInput(now, message = "123 123 123 123", settings = GameSettings(minWordsPerMessage = 2, maxWordsPerMessage = 5)))
//        assertNull(RequestInputValidator.validateInput(now, message = "123 123 123 123 123", settings = GameSettings(minWordsPerMessage = 2, maxWordsPerMessage = 5)))
//        assertNull(RequestInputValidator.validateInput(now, message = "\n 123sd asd   \n ", settings = GameSettings(minWordsPerMessage = 2, maxWordsPerMessage = 5)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = "123", settings = GameSettings(minWordsPerMessage = 2, maxWordsPerMessage = 5)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = " 123 123 123 123 123 123 123 ", settings = GameSettings(minWordsPerMessage = 2, maxWordsPerMessage = 5)))
//        assertErrorWithType(ErrorType.INVALID_MESSAGE, RequestInputValidator.validateInput(now, message = "", settings = GameSettings(minWordsPerMessage = 2, maxWordsPerMessage = 5)))
//    }
//
//    private fun assertErrorWithType(expectedErrorType: ErrorType, actualResponse: ErrorResponse?) {
//        assertNotNull(actualResponse)
//        assertEquals(expectedErrorType, actualResponse!!.errorType)
//    }
//}