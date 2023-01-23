package com.example.selsovid.models.API


@kotlinx.serialization.Serializable
data class RetrieveVCResponse(val accept: Boolean, val vc: String? = null, val  denyReason: String?) {
}