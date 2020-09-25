package com.writinggame

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.*

@SpringBootApplication
class Application

fun main(args : Array<String>) {
    val properties = Properties()
    properties.setProperty("server.port", "443")
    properties.setProperty("security.require-ssl", "true")
    properties.setProperty("server.ssl.key-store", "secrets/keystore.p12")
    properties.setProperty("server.ssl.key-store-password", "")
    properties.setProperty("server.ssl.keyStoreType", "PKCS12")
    properties.setProperty("server.ssl.keyAlias", "tomcat")

    val app = SpringApplication(Application::class.java)
    app.setDefaultProperties(properties)

    app.run(*args)
}

