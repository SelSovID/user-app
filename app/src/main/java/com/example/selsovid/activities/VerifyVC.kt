package com.example.selsovid.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.selsovid.R
import com.example.selsovid.SSICertUtilities
import com.example.selsovid.models.parcels.ChannelCode
import com.example.selsovid.models.websocket.WebSocketMessagePayload
import com.example.selsovid.models.websocket.WebSocketMessageType
import com.example.selsovid.models.websocket.WebsocketMessage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class VerifyVC : AppCompatActivity() {
    private var websocketListener: VerifierWebsocketListener? = null
    private var channel: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_vc)

        channel = intent.getParcelableExtra("channel", ChannelCode::class.java)?.channel!!
        getSharedVCs()
    }

    private fun populateVCs(vcs: List<SSICertUtilities>) {
        val text = findViewById<TextView>(R.id.verify_vc_vc_text)
        text.text = vcs.joinToString("\n") { it.credentialText }
    }

    private fun getSharedVCs() {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("wss://ssi.s.mees.io/api/ws")
            .build()
        websocketListener = VerifierWebsocketListener(channel!!) {
            runOnUiThread {
                populateVCs(it)
            }
        }
        client.newWebSocket(request, websocketListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        websocketListener?.closeSocket()
    }

    private class VerifierWebsocketListener(val channel: String, val onVCsReceived: (List<SSICertUtilities>) -> Unit) : WebSocketListener() {
        private var sock: WebSocket? = null

        override fun onOpen(webSocket: WebSocket, response: Response) {
            sock = webSocket
            val openMsg = WebsocketMessage(WebSocketMessageType.OPEN.type, channel)
            webSocket.send(Json.encodeToString(openMsg))
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val parsed = Json.decodeFromString<WebsocketMessage>(text)
            when (parsed.type){
                WebSocketMessageType.MESSAGE.type -> {
                    val vcStrings = Json.decodeFromString<WebSocketMessagePayload>(parsed.payload!!).VCs
                    val vcs = vcStrings.map { SSICertUtilities.import(it) }
                    onVCsReceived(vcs)
                }
                WebSocketMessageType.CLOSE.type -> {
                    webSocket.close(1000, "")
                }
                WebSocketMessageType.JOIN.type,
                WebSocketMessageType.OPEN.type -> {
                    throw IOException("Invalid websocket message received")
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            sock = null // prevent double close
        }

        fun closeSocket() {
            sock?.close(1000, "")
        }
    }
}
