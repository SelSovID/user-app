package com.example.selsovid.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [VerifiableCredential::class], version = 1, exportSchema = false)
public abstract class VCDatabase : RoomDatabase() {

    abstract fun vcDao(): VcDao

    private class VerifiedCredentialDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }
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