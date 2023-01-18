package com.example.selsovid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.selsovid.activities.MainActivity
import com.example.selsovid.fragments.QRScanner


class ReceivedVC : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_received_vc)
        Log.v("readingVc", "ayaya")
        val textView = findViewById<View>(R.id.showInfo) as TextView



        val info = intent.getParcelableExtra("message", QRScanner.Info::class.java  ).toString()
        Log.v("readingVc", info)

        textView.text = info



        var backbutton = findViewById<Button>(R.id.backButton)

        backbutton.setOnClickListener{
            val intent = Intent (this, MainActivity::class.java)
            this.startActivity(intent)
        }





    }

    fun setInfoToScreen(info: String){
        //Thread.sleep(6000)
        var vcInfo = info


    }


}