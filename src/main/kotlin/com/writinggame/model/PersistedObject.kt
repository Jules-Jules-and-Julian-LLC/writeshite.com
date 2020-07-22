package com.writinggame.model

import org.apache.ibatis.session.SqlSession

const val dbMapperPrefix = "com.writinggame.db.mappers."

abstract class PersistedObject(private var session: SqlSession?) {
    /**
     * Unique key for this object, can be anything
     */
    abstract fun getKey() : Any

    /**
     * Whether this object was pulled from the DB or not
     */
    var fromDb = false

    fun sessionize(session: SqlSession) {
        this.session = session
    }

    fun save() {
        if(session == null) {
            return
        } else {
            if(!fromDb) {
                session!!.insert(dbMapperPrefix + this.javaClass.simpleName + ".insert", this)
                fromDb = true
            } else {
                session!!.update(dbMapperPrefix + this.javaClass.simpleName + ".update", this)
            }
        }
    }

    fun <T : PersistedObject> find() : T {
        val found: T = session!!.selectOne(dbMapperPrefix + this.javaClass.simpleName + ".find", getKey())
        found.fromDb = true
        return found
    }
}