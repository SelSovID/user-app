package com.example.selsovid.models.websocket


enum class WebSocketMessageType(val type: String) {
    OPEN("open"),
    CLOSE("close"),
    MESSAGE("message"),
    JOIN("join"),;

    override fun toString(): String {
        return type
    }
}

@kotlinx.serialization.Serializable
data class WebsocketMessage(val type: String, val channel: String, val payload: String? = null)