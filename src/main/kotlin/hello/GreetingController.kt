package hello

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class GreetingController {
    private val LOGGER: Logger = LoggerFactory.getLogger(javaClass)

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun message(message: ChatMessage): ChatResponse {
        return ChatResponse(HtmlUtils.htmlEscape(message.name) + ": " +HtmlUtils.htmlEscape(message.message))
    }
}