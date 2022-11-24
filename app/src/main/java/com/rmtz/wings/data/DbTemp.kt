package com.rmtz.wings.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp")
data class DbTemp(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "username") val username: String
)