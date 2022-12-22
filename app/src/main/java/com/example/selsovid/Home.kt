package com.example.selsovid

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
            val toast = Toast.makeText(activity, "code genereren", Toast.LENGTH_SHORT)
            toast.show()
        }
        val bluetoothManager: BluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val intent = Intent(context, enableBtIntent::class.java)
            getResult.launch(intent)
            //startActivityForResult(enableBtIntent, 1)
            //bluetoothAdapter.startDiscovery()
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
        var ownAddress = "arr, "
        val text = ownAddress + "dit is een bluetooth adres"
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
        imageView = view?.findViewById(R.id.QrcodeImageView) as ImageView
        imageView.setImageBitmap(bitmap)
    }


}