package com.writinggame.model

class GameSettings(roundTimeMinutes: Long = 5) {
    val roundTimeMinutes = if (roundTimeMinutes <= 0) 5 else roundTimeMinutes
}