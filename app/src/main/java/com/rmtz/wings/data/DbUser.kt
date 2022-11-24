package com.rmtz.wings.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class DbUser(
    @PrimaryKey(autoGenerate = true) val id:Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String
)