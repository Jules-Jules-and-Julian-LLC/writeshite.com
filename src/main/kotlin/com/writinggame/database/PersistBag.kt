package com.writinggame.database

import com.writinggame.model.PersistedObject
import com.writinggame.model.dbMapperPrefix
import org.apache.ibatis.session.SqlSession

open class PersistBag(val session: SqlSession) {
    fun <T : PersistedObject> find(key: Any) : T {
        val found: T = session.selectOne(dbMapperPrefix + this.javaClass.simpleName.replace("Bag", "") + ".find", key)
        found.fromDb = true
        return found
    }

    fun <T: PersistedObject> lenientFind(key: Any): T? {
        val found: T? = session.selectOne(dbMapperPrefix + this.javaClass.simpleName.replace("Bag", "") + ".find", key)
        found?.fromDb = true
        return found
    }
}