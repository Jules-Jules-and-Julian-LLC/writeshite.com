package com.writinggame.model

data class GameLobby(val creator: Player, val players: List<Player> = listOf(creator))