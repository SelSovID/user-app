package com.example.selsovid.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [VerifiableCredential::class], version = 1, exportSchema = false)
abstract class VCDatabase : RoomDatabase() {

    abstract fun vcDao(): VcDao

    private class VerifiedCredentialDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: VCDatabase? = null

        fun getInstance(context: Context): VCDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        fun buildDatabase(context: Context): VCDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                VCDatabase::class.java,
                "selsovid_database"
            )
                .build()
        }
    }
}