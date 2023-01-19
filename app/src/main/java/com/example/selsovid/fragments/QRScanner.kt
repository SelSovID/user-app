package com.example.selsovid.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.selsovid.activities.MainActivity
import com.example.selsovid.R
import com.example.selsovid.WebSocketUtilities
import com.example.selsovid.activities.VerifyVC
import com.example.selsovid.models.parcels.ChannelCode
import kotlinx.android.parcel.Parcelize
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
                val channel = ChannelCode(it.text)
                val intent = Intent(activity, VerifyVC::class.java)
                intent.putExtra("channel", channel)
                activity.startActivity(intent)
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
}