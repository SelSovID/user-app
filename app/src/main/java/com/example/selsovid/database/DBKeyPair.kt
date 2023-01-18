package com.example.selsovid.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keypair")
data class DBKeyPair(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "public")
    val publicKey: ByteArray,
    @ColumnInfo(name = "private")
    val privateKey: ByteArray
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DBKeyPair

        if (!publicKey.contentEquals(other.publicKey)) return false
        if (!privateKey.contentEquals(other.privateKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = publicKey.contentHashCode()
        result = 31 * result + privateKey.contentHashCode()
        return result
    }
}