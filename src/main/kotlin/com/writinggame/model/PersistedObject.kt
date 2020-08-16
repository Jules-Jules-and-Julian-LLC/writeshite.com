package com.writinggame.model

import org.apache.ibatis.session.SqlSession

const val dbMapperPrefix = "com.writinggame.db.mappers."

abstract class PersistedObject(private val session: SqlSession?) {
    /**
     * Unique key for this object, can be anything
     */
    abstract fun getKey() : Any

    /**
     * Whether this object was pulled from the DB or not
     */
    var fromDb = false

    fun <T: PersistedObject> save(): T {
        if (session == null) {
            throw Exception("Can't save on unsessionized object")
        }

        if (!fromDb) {
            session.insert(dbMapperPrefix + this.javaClass.simpleName + ".insert", this)
            fromDb = true
        } else {
            session.update(dbMapperPrefix + this.javaClass.simpleName + ".update", this)
        }

        //TODO can be just return this?
        return findDbMe()
    }

    fun delete() {
        if (session == null) {
            throw Exception("Can't delete an unsessionized object")
        }

        session.delete(dbMapperPrefix + this.javaClass.simpleName + ".delete", getKey())
    }

    fun <T: PersistedObject> findDbMe(): T {
        if (session == null) {
            throw Exception("Can't find on unsessionized object")
        }

        val found: T = session.selectOne(dbMapperPrefix + this.javaClass.simpleName + ".find", getKey())
        found.fromDb = true
        return found
    }
}