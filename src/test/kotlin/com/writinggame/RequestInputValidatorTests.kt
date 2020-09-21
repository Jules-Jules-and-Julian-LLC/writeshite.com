package com.writinggame

import com.writinggame.controller.RequestInputValidator
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.ZonedDateTime

class RequestInputValidatorTests {
    @Test
    fun lobbyIdValid() {
        assertNull(RequestInputValidator.validateInput(ZonedDateTime.now(), "123"))
        assertNull(RequestInputValidator.validateInput(ZonedDateTime.now(), "as-1323__--123oiqweuASDZXCddASDASDdf"))
        //64 chars is max
        assertNull(RequestInputValidator.validateInput(ZonedDateTime.now(), "asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf1234"))

        //65 characters is invalid
        assertNotNull(RequestInputValidator.validateInput(ZonedDateTime.now(), "asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf12345"))
        //empty is invalid
        assertNotNull(RequestInputValidator.validateInput(ZonedDateTime.now(), ""))
        //non-alphanumeric is invalid
        assertNotNull(RequestInputValidator.validateInput(ZonedDateTime.now(), "!"))
    }

    @Test
    fun usernameValid() {
        assertNull(RequestInputValidator.validateInput(ZonedDateTime.now(), username = "123"))
        assertNull(RequestInputValidator.validateInput(ZonedDateTime.now(), username = "as-1323__--123oiqweuASDZXCddASDASDdf"))
        //64 chars is max
        assertNull(RequestInputValidator.validateInput(ZonedDateTime.now(), username = "asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf1234"))

        //65 characters is invalid
        assertNotNull(RequestInputValidator.validateInput(ZonedDateTime.now(), username = "asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf123asdf12345"))
        assertNotNull(RequestInputValidator.validateInput(ZonedDateTime.now(), username = "really long and obnoxious name asdf areally long and obnoxious name asdf a2"))
        //empty is invalid
        assertNotNull(RequestInputValidator.validateInput(ZonedDateTime.now(), username = ""))
    }
}