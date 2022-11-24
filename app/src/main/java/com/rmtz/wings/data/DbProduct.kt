package com.rmtz.wings.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class DbProduct(
    @PrimaryKey(autoGenerate = true) val id:Long,
    @ColumnInfo(name = "product_code") val code: String,
    @ColumnInfo(name = "product_name") val name: String,
    @ColumnInfo(name = "price") val price: Long,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "discount") val discount: Long,
    @ColumnInfo(name = "dimension") val dimension: String,
    @ColumnInfo(name = "unit") val unit: String,
)