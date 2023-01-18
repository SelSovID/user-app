package com.example.selsovid.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KpDao {
    @Query("SELECT * FROM keypair limit 1")
    fun getPair(): DBKeyPair?

    @Insert
    fun createPair(pair: DBKeyPair)
}
