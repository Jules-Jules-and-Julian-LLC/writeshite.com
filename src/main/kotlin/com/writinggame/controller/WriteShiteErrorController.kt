package com.writinggame.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

//Crappy name because ErrorController is in use
@Controller
class WriteShiteErrorController : ErrorController {

    /**
     * Redirect all errors to index. This handles any 404s.
     */
    @RequestMapping("/error")
    @ResponseBody
    fun handleError(request: HttpServletRequest): String {
        return "<head>" +
                "  <meta http-equiv=\"refresh\" content=\"0; URL=/\" />" +
                "</head>"
    }
}