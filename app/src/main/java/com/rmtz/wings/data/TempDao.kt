package com.rmtz.wings.data

import androidx.room.*

@Dao
interface TempDao {
    @Query("SELECT * FROM `temp`")
    fun getTemp(): List<DbTemp>

    @Insert
    fun insertTemp(vararg temp: DbTemp)

    @Update
    fun updateTemp(vararg temp: DbTemp)

    @Delete
    fun deleteTemp(vararg temp: DbTemp)
}