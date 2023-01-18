package com.example.selsovid.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VcDao {
    //get all
    @Query("SELECT * FROM verified_credentials_table")
    fun getAllVCs(): Flow<List<VerifiableCredential>>

    //insert!
    @Insert()
    fun insert(VC: VerifiableCredential)

    @Update
    fun update(verifiableCredential: VerifiableCredential)

    @Delete
    fun delete(verifiableCredential: VerifiableCredential)
}