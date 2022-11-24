package com.rmtz.wings.data

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getListUser(): List<DbUser>

    @Insert
    fun insertUser(vararg user: DbUser)

    @Query("SELECT * FROM user WHERE username=:username AND password=:password")
    fun getLogin(username: String, password: String): DbUser
}