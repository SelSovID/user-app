package com.example.selsovid.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verified_credentials_table")
data class VerifiableCredential(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "vc_id") val id: Int? = null,

    @ColumnInfo(name = "vc_data") val data: ByteArray,

    @ColumnInfo(name = "request_id") val requestId: String? = null,

    @ColumnInfo(name = "vc_text") val text: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VerifiableCredential

        if (id != other.id) return false
        if (!data.contentEquals(other.data)) return false
        if (requestId != other.requestId) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + data.contentHashCode()
        result = 31 * result + (requestId?.hashCode() ?: 0)
        result = 31 * result + text.hashCode()
        return result
    }
}