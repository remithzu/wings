package com.rmtz.wings.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_detail")
data class DbTransactionDetail(
    @PrimaryKey(autoGenerate = true) val id:Long,
    @ColumnInfo(name = "transaction_id") val transactionId: Long,
    @ColumnInfo(name = "document_code") val code: String,
    @ColumnInfo(name = "document_number") val number: String,
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "product_code") val productCode: String,
    @ColumnInfo(name = "price") val price: Long,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "unit") val unit: String,
    @ColumnInfo(name = "sub_total") val subTotal: Long,
    @ColumnInfo(name = "currency") val currency: String,
)