package com.writinggame

import com.writinggame.database.LobbyBag
import com.writinggame.database.PlayerBag
import com.writinggame.database.WriteShiteSessionFactory
import com.writinggame.model.Player
import org.apache.ibatis.session.SqlSession
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class Application

fun main(args : Array<String>) {
    WriteShiteSessionFactory.openSession().use { session: SqlSession ->
        val player = Player(session, clientId = "asdf", username = "me", lobbyId = "orio")
        player.save()
        val player2: Player = player.find()


        val list: List<Player> = PlayerBag(session).findPlayers()
        for (player3 in list) {
            println("Player: ${player3.username}, lobby: ${player3.lobbyId}, clientID: ${player3.clientId}")
        }

//        LobbyBag(session).createLobbyIfNotExists("myLobby")

        session.commit()
    }
    SpringApplication.run(Application::class.java, *args)
}

