package com.example.selsovid.jsonModel

import kotlinx.serialization.Serializable

@Serializable
data class PostVCRequest(val vc: String, val attachedVCs: Array<String>, val issuerId: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostVCRequest

        if (vc != other.vc) return false
        if (!attachedVCs.contentEquals(other.attachedVCs)) return false
        if (issuerId != other.issuerId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vc.hashCode()
        result = 31 * result + attachedVCs.contentHashCode()
        result = 31 * result + issuerId
        return result
    }
}

@Serializable
data class PostVCResponse(val id: String)