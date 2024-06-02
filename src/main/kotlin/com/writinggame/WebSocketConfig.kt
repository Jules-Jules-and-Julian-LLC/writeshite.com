package com.writinggame

import com.writinggame.controller.viewModels.JoinGameResponse
import com.writinggame.model.LobbyManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.time.ZonedDateTime
import java.util.*


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    @Lazy
    @Autowired
    lateinit var messagingTemplate: SimpMessageSendingOperations

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic/", "/user/", "/queue/", "/websocket/", "/socket/", "/info/")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket")
            .setAllowedOrigins("http://localhost:3000", "http://localhost", "http://localhost:8080", "https://writeshite.com", "http://writeshite.com",
                "ws://localhost", "ws://localhost/websocket", "ws://localhost:8080/",  "ws://localhost:8080/websocket")
            .withSockJS()
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:3000", "http://localhost", "http://localhost:8080", "https://writeshite.com", "http://writeshite.com",
                "ws://localhost", "ws://localhost/websocket", "ws://localhost:8080/",  "ws://localhost:8080/websocket")
            .withSockJS()
    }

    @EventListener
    fun onSocketDisconnected(event: SessionDisconnectEvent) {
        val sha = StompHeaderAccessor.wrap(event.message)
        if (sha.sessionId != null) {
            val receivedDatetime = ZonedDateTime.now()
            val lobby = LobbyManager.leaveLobby(sha.sessionId!!)
            if (lobby != null) {
                messagingTemplate.convertAndSend(
                    "/topic/lobby.${lobby.lobbyId.uppercase(Locale.getDefault())}",
                        JoinGameResponse(lobby, receivedDatetime)
                )
            }
        }
        println("[Disconnected] " + sha.sessionId)
    }
}