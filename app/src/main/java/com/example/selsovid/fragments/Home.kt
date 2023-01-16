package com.example.selsovid.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.selsovid.R
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.selsovid.SendRequest
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import okhttp3.*
import okio.ByteString
import java.util.*
import java.util.concurrent.TimeUnit


class Home : Fragment() {
    lateinit var imageView: ImageView
    lateinit var mView: View

    lateinit var randomUUID: UUID




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        val generateButton = mView.findViewById(R.id.btnGenerate) as? Button
        val requestButton = mView.findViewById(R.id.gotoRequest) as? Button



        generateButton?.setOnClickListener{
            generateCode()
        }

        requestButton?.setOnClickListener{
            val intent = Intent (getActivity(), SendRequest::class.java)
            getActivity()?.startActivity(intent)
        }


        return mView
    }



    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.getStringExtra("input")
            }
        }



    fun generateCode(){
        setRandomAPIConnectionCode()
        val connectCode = randomUUID.toString()
        connectToAPI()
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(connectCode, BarcodeFormat.QR_CODE, 500, 500)
        imageView = view?.findViewById(R.id.QrcodeImageView) as ImageView
        imageView.setImageBitmap(bitmap)
    }

    fun setRandomAPIConnectionCode() {
        randomUUID = UUID.randomUUID()
    }

    fun getRandomAPIConnectionCode(): UUID{
        return randomUUID
    }

    fun connectToAPI(){
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("wss://ssi.s.mees.io/api/ws")
            .build()
        var listener = EchoWebSocketListener()
        listener.setUUID(getRandomAPIConnectionCode())
        client.newWebSocket(request, listener )
    }


    class EchoWebSocketListener : WebSocketListener() {
        private lateinit var randomUUID: UUID
        override fun onOpen(webSocket: WebSocket, response: Response) {

            Log.v("testbed", randomUUID.toString())

            webSocket.send("{\"type\":\"open\", \"channel\":\"$randomUUID\"}")
            webSocket.send("{\"type\":\"message\", \"channel\":\"$randomUUID\", \"payload\":\"homeTest\"}")
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

        fun setUUID(uuid: UUID){
            randomUUID = uuid
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
