package com.example.selsovid.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.liveData
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.selsovid.MainActivity
import com.example.selsovid.R
import com.example.selsovid.ReceivedVC
import com.example.selsovid.WebSocketUtilities
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit


class QRScanner : Fragment(){
        private lateinit var codeScanner: CodeScanner
        var activity: MainActivity? = getActivity() as MainActivity?
        var receivedCode = ""


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View {
            var binding = inflater.inflate(R.layout.fragment_q_r_scanner, container, false)
            return binding
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
            val activity = requireActivity()
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback {
                activity.runOnUiThread {
                    receivedCode = it.text
                    //Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()

                        val client = OkHttpClient.Builder()
                            .readTimeout(3, TimeUnit.SECONDS)
                            .build()
                        val request = Request.Builder()
                            .url("wss://ssi.s.mees.io/api/ws")
                            .build()
                        val listener = WebSocketUtilities.WebsocketConnection(it.text, false)
                        client.newWebSocket(request, listener)
                        //Thread.sleep(2500)

                        var receivedMessage = listener.getMessage()
                        var info = Info(receivedMessage)
                        startReceivedVC(info)




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
    fun startReceivedVC(setMessage: Info){
        //ReceivedVC().setInfoToScreen(setMessage)
        Log.v("startReceivedVC", setMessage.toString())
        //var messagewithtype =  Info(setMessage)
        val intent = Intent (getActivity(), ReceivedVC::class.java)
        intent.putExtra("message", setMessage)
        startActivity(intent)
    }

    @Parcelize
    data class Info(val name : String) : Parcelable {

    }

//    fun setMessage(message: String){
//
//    }

//    class sendToNewActivity(val message: String): Fragment(){
//
//
//
//        fun send(message: String) {
//            Log.v("startReceivedVC", message)
//            val intent = Intent(activity, ReceivedVC::class.java)
//            intent.putExtra("message", message)
//            startActivity(intent)
//        }
//    }







}