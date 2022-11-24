package com.rmtz.wings.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DbUser::class, DbProduct::class, DbTransaction::class, DbTransactionDetail::class, DbTemp::class, DbTransactionTemp::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun UserDao(): UserDao
    abstract fun ProductDao(): ProductDao
    abstract fun TransactionDao(): TransactionDao
    abstract fun TempDao(): TempDao
}