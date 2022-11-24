package com.rmtz.wings.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class DbTransaction(
    @PrimaryKey(autoGenerate = true) val id:Long,
    @ColumnInfo(name = "document_code") val code: String,
    @ColumnInfo(name = "document_number") val number: String,
    @ColumnInfo(name = "user") val user: String,
    @ColumnInfo(name = "total") val total: Long,
    @ColumnInfo(name = "date") val date: String,
)