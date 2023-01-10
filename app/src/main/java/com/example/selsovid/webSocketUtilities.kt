package com.example.selsovid

import android.util.Log
import com.google.gson.Gson
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.*

class webSocketUtilities {
    enum class MessageType(val typeStr: String){
        OPEN("open"),
        CLOSE("close"),
        MESSAGE("message"),
        JOIN("join")
    }
    @Serializable
    data class webSocketMessage(val type: MessageType, val channel: String, val payload: String = "")

    class WebsocketConnection(val channelID: String) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {

            Log.v("testbed", channelID)

            var openMessage = webSocketMessage(MessageType.OPEN, channelID)
            webSocket.send(Json.encodeToString(openMessage))

            //webSocket.send("{\"type\":\"open\", \"channel\":\"$channelID\"}")
            //webSocket.send("{\"type\":\"message\", \"channel\":\"$channelID\", \"payload\":\"homeTest\"}")
            //webSocket.send("{\"type\":\"close\", \"channel\":\"$randomUUID\"}")
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            var message = Gson().fromJson(text, Message::class.java)
            //if(message.payload == )

            output("Receiving : $message")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            output("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
            output("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            output("Error : " + t.message)
        }

        companion object {
            private val NORMAL_CLOSURE_STATUS = 1000
        }

        private fun output(txt: String) {
            Log.v("webSocket", txt)
        }



        class Message {
            var type: String = ""
            var channel: String = ""
            var payload: String = ""

            override fun toString(): String {
                return "Message(type=$type, channel=$channel, payload=$payload)"
            }
        }






    }


}