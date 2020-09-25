package com.writinggame

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.*

@SpringBootApplication
class Application

fun main(args : Array<String>) {
    val properties = Properties()
    properties.setProperty("server.port", "80")

    val app = SpringApplication(Application::class.java)
    app.setDefaultProperties(properties)

    app.run(*args)
}

