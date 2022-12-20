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
    lateinit var mView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        val generateButton = mView.findViewById(R.id.btnGenerate) as? Button

        generateButton?.setOnClickListener{
            generateCode()
            //val toast = Toast.makeText(activity, "code genereren", Toast.LENGTH_SHORT)
            //toast.show()
        }

        return mView
    }

    fun generateCode(){
        var ownAddress = "arr, "
        val text = ownAddress + "dit is een bluetooth adres"
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
        imageView = view?.findViewById(R.id.QrcodeImageView) as ImageView
        imageView.setImageBitmap(bitmap)
    }







}