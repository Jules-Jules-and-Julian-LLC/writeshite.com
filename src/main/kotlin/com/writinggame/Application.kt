package com.writinggame

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.*

@SpringBootApplication
class Application

fun main(args : Array<String>) {
//    SpringApplication.run(Application::class.java, *args)
    val properties = Properties()
    properties.setProperty("server.port", "1337")

    val app = SpringApplication(Application::class.java)
    app.setDefaultProperties(properties)

    app.run(*args)
}

