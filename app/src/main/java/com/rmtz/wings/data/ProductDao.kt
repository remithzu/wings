package com.rmtz.wings.data

import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getListProduct(): List<DbProduct>

    @Query("SELECT * FROM product WHERE id=:id")
    fun getProduct(id: Long): List<DbProduct>

    @Query("SELECT max(id) FROM product")
    fun getLatestId(): Long

    @Insert
    fun insertProduct(vararg product: DbProduct)

    @Update
    fun updateProduct(vararg product: DbProduct)

    @Delete
    fun deleteProduct(vararg product: DbProduct)
}