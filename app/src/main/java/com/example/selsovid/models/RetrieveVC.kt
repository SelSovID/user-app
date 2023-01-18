package com.example.selsovid.models


@kotlinx.serialization.Serializable
data class RetrieveVCResponse(val accept: Boolean, val vc: String? = null) {
}