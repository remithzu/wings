package com.rmtz.wings.data

import androidx.room.*

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction`")
    fun getListTransaction(): List<DbTransaction>

    @Query("SELECT * FROM `transaction` WHERE id=:id")
    fun getTransaction(id: Long): List<DbTransaction>

    @Query("SELECT max(id) FROM `transaction`")
    fun getTransactionLatestId(): Long

    @Insert
    fun insertTransaction(vararg product: DbTransaction)

    @Update
    fun updateTransaction(vararg product: DbTransaction)

    @Delete
    fun deleteTransaction(vararg product: DbTransaction)

    /* ------------- Detail Transaction ------------- */
    @Query("SELECT * FROM transaction_detail")
    fun getListDetailTrans(): List<DbTransactionDetail>

    @Query("SELECT * FROM transaction_detail WHERE transaction_id=:id")
    fun getDetailTrans(id: Long): List<DbTransactionDetail>

    @Query("SELECT * FROM transaction_detail WHERE transaction_id=:id")
    fun getTransJoin(id: Long): List<DbTransactionDetail>

    @Query("SELECT max(id) FROM transaction_detail")
    fun getDetailTransLatestId(): Long

    @Insert
    fun insertDetailTrans(vararg product: DbTransactionDetail)

    @Update
    fun updateDetailTrans(vararg product: DbTransactionDetail)

    @Delete
    fun deleteDetailTrans(vararg product: DbTransactionDetail)

    @Query("DELETE FROM transaction_detail")
    fun deleteDetailTransAll()

    /* ------------- Detail Transaction Temp ------------- */
    @Query("SELECT * FROM transaction_temp")
    fun getListTemp(): List<DbTransactionTemp>

    @Query("SELECT * FROM transaction_temp WHERE product_id=:id")
    fun getProduct(id: Long): List<DbTransactionTemp>

    @Query("SELECT * FROM transaction_temp WHERE product_id=:id LIMIT 1")
    fun getLastItem(id: Long): DbTransactionTemp

    @Query("SELECT max(id) FROM transaction_temp")
    fun getTempLastId(): Long

    @Insert
    fun insertTemp(vararg product: DbTransactionTemp)

    @Delete
    fun deleteTemp(vararg product: DbTransactionTemp)

    @Query("DELETE FROM transaction_temp")
    fun deleteTempAll()
}