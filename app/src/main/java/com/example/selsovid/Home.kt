package com.example.selsovid

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import java.io.IOException
import java.util.UUID


class Home : Fragment() {
    lateinit var imageView: ImageView
    lateinit var mView: View
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    val myUUID: UUID = "a0f040b4-a199-4c61-b006-8bd737970696" as UUID


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        val generateButton = mView.findViewById(R.id.btnGenerate) as? Button

        generateButton?.setOnClickListener{
            generateCode()
            val toast = Toast.makeText(activity, "code genereren", Toast.LENGTH_SHORT)
            toast.show()
        }
        bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val intent = Intent(context, enableBtIntent::class.java)
            //var ownAddress = getOwnAddress()

            getResult.launch(intent)


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

    @SuppressLint("MissingPermission")
    fun getOwnAddress(): String{
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        val intent = Intent(context, enableBtIntent::class.java)
        Log.println(Log.INFO,"BTadrresskutding",BluetoothAdapter.getDefaultAdapter().address.toString())
        return BluetoothAdapter.getDefaultAdapter().address.toString()
    }

    fun generateCode(){
        val ownAddress = getOwnAddress()
        val text = ownAddress
        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
        imageView = view?.findViewById(R.id.QrcodeImageView) as ImageView
        imageView.setImageBitmap(bitmap)
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {


        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("SelSovID", myUUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    private fun manageMyConnectedSocket(it: BluetoothSocket) {
        val handler: Handler = Handler()

        var mConnectedThread = MyBluetoothService(handler).ConnectedThread(it)
        mConnectedThread.start()
    }


}