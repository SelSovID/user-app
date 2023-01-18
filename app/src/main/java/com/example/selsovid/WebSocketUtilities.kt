package com.example.selsovid

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.selsovid.fragments.QRScanner
import com.google.gson.Gson
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketUtilities: Fragment(){
    var receivedMessage = ""




    enum class MessageType(val typeStr: String){
        OPEN("open"),
        CLOSE("close"),
        MESSAGE("message"),
        JOIN("join")
    }
    @Serializable
    data class webSocketMessage(val type: String, val channel: String, val payload: String = "")

    class WebsocketConnection(val channelID: String, val isHolder: Boolean) : WebSocketListener() {

        var vcMessage = ""

        val messageReceived: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        //val liveData: LiveData<String> get() = messageReceived


        override fun onOpen(webSocket: WebSocket, response: Response) {

            Log.v("testbed", channelID)

            var openMessage = webSocketMessage(MessageType.OPEN.typeStr, channelID)
            var closeMessage = webSocketMessage(MessageType.CLOSE.typeStr, channelID)
            var dataMessage = webSocketMessage(MessageType.MESSAGE.typeStr, channelID, "messagetest")
            if(isHolder){
                webSocket.send(Json.encodeToString(openMessage))
                Log.v("holder send", openMessage.toString())
                //while (true) {
                //    Thread.sleep(5000)
                //    webSocket.send(Json.encodeToString(dataMessage))
                //}
            } else {
                Log.v("verifier send", openMessage.toString())

                webSocket.send(Json.encodeToString(openMessage))
                //Thread.sleep(5000)

                //Log.v("verifier", closeMessage.toString())
                //webSocket.send(Json.encodeToString(closeMessage))

            }
            //webSocket.send("{\"type\":\"open\", \"channel\":\"$channelID\"}")
            //webSocket.send("{\"type\":\"message\", \"channel\":\"$channelID\", \"payload\":\"homeTest\"}")
            //webSocket.send("{\"type\":\"close\", \"channel\":\"$randomUUID\"}")
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            var dataMessage = webSocketMessage(MessageType.MESSAGE.typeStr, channelID, "messagetest")
            var message = Gson().fromJson(text, Message::class.java)
            var closeMessage = webSocketMessage(MessageType.CLOSE.typeStr, channelID)
            Log.v("onmessage ALLES", message.type + MessageType.OPEN.typeStr + isHolder)
            if(message.type == MessageType.JOIN.typeStr && isHolder){
                webSocket.send(Json.encodeToString(dataMessage))
                webSocket.send(Json.encodeToString(closeMessage))
                webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
            } else if(message.type == MessageType.MESSAGE.typeStr && !isHolder) {
                vcMessage = message.toString()
                Log.v("Message to verifier = ", message.payload)
                webSocket.send(Json.encodeToString(closeMessage))
                webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
            }










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

        private fun outputData(string: String) {
            messageReceived.postValue(string)
        }

        fun getMessage(): String {
            Log.v("in getmessage", vcMessage)

            return vcMessage
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

//    fun startReceivedVC(setMessage: String){
//        //ReceivedVC().setInfoToScreen(setMessage)
//        Log.v("startReceivedVC", setMessage)
//        var messagewithtype =  Info(setMessage)
//        val intent = Intent (activity, ReceivedVC::class.java)
//        intent.putExtra("message", setMessage)
//        startActivity(intent)
//    }
//
//    @Parcelize
//    data class Info(val name : String) : Parcelable {
//
//    }







}