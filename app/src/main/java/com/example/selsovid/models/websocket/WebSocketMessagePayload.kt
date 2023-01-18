package com.example.selsovid.models.websocket

@kotlinx.serialization.Serializable
data class WebSocketMessagePayload(val VCs: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebSocketMessagePayload

        if (!VCs.contentEquals(other.VCs)) return false

        return true
    }

    override fun hashCode(): Int {
        return VCs.contentHashCode()
    }
}