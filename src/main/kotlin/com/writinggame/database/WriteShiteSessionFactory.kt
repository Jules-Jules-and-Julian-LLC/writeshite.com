package com.writinggame.database

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import java.io.InputStream

object WriteShiteSessionFactory {
    private val sqlSessionFactory: SqlSessionFactory = buildSqlSessionFactory()

    private fun buildSqlSessionFactory(): SqlSessionFactory {
        val resource = "mybatis-config.xml"
        val inputStream: InputStream = Resources.getResourceAsStream(resource)
        return SqlSessionFactoryBuilder().build(inputStream)
    }

    fun openSession(): SqlSession {
        return sqlSessionFactory.openSession()
    }
}