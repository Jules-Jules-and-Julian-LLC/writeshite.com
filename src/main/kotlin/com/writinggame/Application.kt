package com.writinggame

import com.writinggame.database.PlayerBag
import com.writinggame.database.WriteShiteSessionFactory
import com.writinggame.model.Player
import org.apache.ibatis.session.SqlSession
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class Application

fun main(args : Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

