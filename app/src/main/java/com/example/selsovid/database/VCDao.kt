package com.example.selsovid.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VcDao {
    //get all
    @Query("SELECT * FROM verified_credentials_table")
    fun getAllVCs(): Flow<List<VerifiableCredential>>

//    //insert!
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(VC: VerifiableCredential)

    //delete all
    @Query("DELETE FROM verified_credentials_table")
    fun deleteAll()
}