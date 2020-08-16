package com.writinggame.database

import org.apache.ibatis.session.SqlSession

class StoryBag(session: SqlSession): PersistBag(session) {
}