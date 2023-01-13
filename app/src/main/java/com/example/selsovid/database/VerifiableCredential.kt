package com.example.selsovid.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verified_credentials_table")
data class VerifiableCredential(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "vc_id") val vc_id: Int? = null,

    @ColumnInfo(name = "vc_parent_id") val vc_parent_id:Int?,

    @ColumnInfo(name = "vc_name") val vc_name:String,

    @ColumnInfo(name = "vc_request_text") val vc_request_text:String,

    @ColumnInfo(name = "vc_issuer_signature") val vc_issuer_signature:String?,

    @ColumnInfo(name =  "vc_issuer_public_key") val vc_issuer_public_key:String?,

    @ColumnInfo(name =  "vc_requested") val vc_requested:String?,

    @ColumnInfo(name =  "vc_granted") val vc_granted:String?,

    @ColumnInfo(name =  "vc_last_used") val vc_last_used:String?
)