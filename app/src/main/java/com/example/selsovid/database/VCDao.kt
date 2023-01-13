package com.example.selsovid.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VcDao {
    //get all
    @Query("SELECT * FROM verified_credentials_table")
    fun getAllVCs(): Array<VerifiableCredential>

//    //insert!
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(VC: VerifiableCredential)

    //delete all
    @Query("DELETE FROM verified_credentials_table")
    fun deleteAll()
}