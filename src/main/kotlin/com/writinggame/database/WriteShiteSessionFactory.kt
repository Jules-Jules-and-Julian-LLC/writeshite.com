package com.writinggame.database

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import java.io.InputStream

object WriteShiteSessionFactory {
    fun openSession(): SqlSession {
        val resource = "mybatis-config.xml"
        val inputStream: InputStream = Resources.getResourceAsStream(resource)
        val sqlSessionFactory: SqlSessionFactory = SqlSessionFactoryBuilder().build(inputStream)

        return sqlSessionFactory.openSession()
    }
}