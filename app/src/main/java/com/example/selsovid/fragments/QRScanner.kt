package com.example.selsovid.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.selsovid.MainActivity
import com.example.selsovid.R
import com.example.selsovid.webSocketUtilities
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import java.util.*
import java.util.concurrent.TimeUnit


class QRScanner : Fragment(){
        private lateinit var codeScanner: CodeScanner
        var activity: MainActivity? = getActivity() as MainActivity?

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View {
            return inflater.inflate(R.layout.fragment_q_r_scanner, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
            val activity = requireActivity()
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback {
                activity.runOnUiThread {
                    Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                    val client = OkHttpClient.Builder()
                        .readTimeout(3, TimeUnit.SECONDS)
                        .build()
                    val request = Request.Builder()
                        .url("wss://ssi.s.mees.io/api/ws")
                        .build()
                    var listener = webSocketUtilities.WebsocketConnection(it.text, false)
                    //listener.setUUID(UUID.fromString(it.text))
                    client.newWebSocket(request, listener )

                }
            }
            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }

        }

        override fun onResume() {
            super.onResume()
            codeScanner.startPreview()
        }

        override fun onPause() {
            codeScanner.releaseResources()
            super.onPause()
        }

    class EchoWebSocketListener : WebSocketListener() {
        private lateinit var socketUUID: UUID
        override fun onOpen(webSocket: WebSocket, response: Response) {

            Log.v("testbed", socketUUID.toString())

            webSocket.send("{\"type\":\"open\", \"channel\":\"$socketUUID\"}")
            webSocket.send("{\"type\":\"message\", \"channel\":\"$socketUUID\", \"payload\":\"scannertesting\"}")
            webSocket.send("{\"type\":\"close\", \"channel\":\"$socketUUID\"}")
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            var message = Gson().fromJson(text, Message::class.java)

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

        fun setUUID(uuid: UUID){
            socketUUID = uuid
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