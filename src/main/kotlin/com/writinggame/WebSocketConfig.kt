package com.writinggame

import com.writinggame.controller.viewModels.JoinGameResponse
import com.writinggame.model.LobbyManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.time.ZonedDateTime


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    @Autowired
    lateinit var messagingTemplate: SimpMessageSendingOperations;

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic/", "/user/", "/queue/")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket").withSockJS()
    }

    @EventListener
    fun onSocketDisconnected(event: SessionDisconnectEvent) {
        val sha = StompHeaderAccessor.wrap(event.message)
        if(sha.sessionId != null) {
            val receivedDatetime = ZonedDateTime.now()
            val lobby = LobbyManager.leaveLobby(sha.sessionId!!)
            if(lobby != null) {
                messagingTemplate.convertAndSend(
                    "/topic/lobby.${lobby.lobbyId}",
                    JoinGameResponse(lobby, receivedDatetime)
                )
            }
        }
        println("[Disconnected] " + sha.sessionId)
    }
}