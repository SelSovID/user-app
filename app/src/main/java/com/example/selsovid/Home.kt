package com.example.selsovid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder



class Home : Fragment() {
    lateinit var imageView: ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    fun generateCode(view: View){
        val text = "ayaya"
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
        imageView = view?.findViewById<ImageView>(R.id.myImageView)!!

        if (imageView != null) {
            imageView.setImageBitmap(bitmap)
            //val toast = Toast.makeText(context, "epico", Toast.LENGTH_SHORT)
            //toast.show()
        } else {
            imageView.setImageBitmap(bitmap)
            val toast = Toast.makeText(context, "broke", Toast.LENGTH_SHORT)
            toast.show()
        }
    }





}