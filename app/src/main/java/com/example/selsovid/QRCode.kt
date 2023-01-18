package com.example.selsovid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import java.util.concurrent.TimeUnit

class QRCode: AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var mView: View

    lateinit var randomUUID: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_q_r_code)
        var backbutton = findViewById<Button>(R.id.backButton)

        generateCode()

        backbutton.setOnClickListener{
            val intent = Intent (this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }


    fun generateCode(){
        setRandomAPIConnectionCode()
        val connectCode = randomUUID.toString()
        connectToAPI()
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(connectCode, BarcodeFormat.QR_CODE, 500, 500)
        imageView = findViewById(R.id.QrcodeImageView)
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
        var listener = WebSocketUtilities.WebsocketConnection(getRandomAPIConnectionCode().toString(), true)
        //listener.setUUID(getRandomAPIConnectionCode())
        client.newWebSocket(request, listener )
    }
}