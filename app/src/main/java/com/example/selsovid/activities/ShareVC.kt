package com.example.selsovid.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.selsovid.R
import com.example.selsovid.models.parcels.AttachedVCs
import com.example.selsovid.models.websocket.WebSocketMessagePayload
import com.example.selsovid.models.websocket.WebSocketMessageType
import com.example.selsovid.models.websocket.WebsocketMessage
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class ShareVC : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var attachedVCs: List<String>
    private val randomUUID: UUID = UUID.randomUUID()
    private var websocketListener: HolderWebSocketHandler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_vc)
        val backButton = findViewById<Button>(R.id.backButton)
        imageView = findViewById(R.id.QrcodeImageView)

        attachedVCs = intent.getParcelableExtra("attachedVCs", AttachedVCs::class.java)?.data!!
        backButton.setOnClickListener{
            websocketListener?.closeSocket()
            val intent = Intent (this, MainActivity::class.java)
            this.startActivity(intent)
        }
        displayQRCode()
        startWebSocketListener()
    }


    private fun displayQRCode(){
        val connectCode = randomUUID.toString()
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(connectCode, BarcodeFormat.QR_CODE, 500, 500)
        imageView.setImageBitmap(bitmap)
    }



    private fun startWebSocketListener(){
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("wss://ssi.s.mees.io/api/ws")
            .build()
        websocketListener = HolderWebSocketHandler(randomUUID.toString(), attachedVCs.toTypedArray())
        client.newWebSocket(request, websocketListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        websocketListener?.closeSocket()
    }

    private class HolderWebSocketHandler(val channel: String, val VCsToSend: Array<String>): WebSocketListener() {
        private var sock: WebSocket? = null

        override fun onOpen(webSocket: WebSocket, response: Response) {
            sock = webSocket
            val openMsg = WebsocketMessage(WebSocketMessageType.OPEN.type, channel)
            webSocket.send(Json.encodeToString(openMsg))
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val parsed = Json.decodeFromString<WebsocketMessage>(text)
            when (parsed.type){
                WebSocketMessageType.JOIN.type -> {
                    val vcPayload = Json.encodeToString(WebSocketMessagePayload(VCsToSend))
                    val wsMsg = Json.encodeToString(WebsocketMessage(WebSocketMessageType.MESSAGE.type, channel, vcPayload))
                    webSocket.send(wsMsg)
                    val closeMsg = Json.encodeToString(WebsocketMessage(WebSocketMessageType.CLOSE.type, channel))
                    webSocket.send(closeMsg)
                    closeSocket()
                }
                WebSocketMessageType.CLOSE.type -> {
                    webSocket.close(1000, "")
                }
                WebSocketMessageType.MESSAGE.type,
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