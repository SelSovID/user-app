package com.example.selsovid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback


class QRScanner : Fragment(){
        private lateinit var codeScanner: CodeScanner
        var activity: MainActivity? = getActivity() as MainActivity?

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View {
            return inflater.inflate(R.layout.fragment_q_r_scanner, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
            val activity = requireActivity()
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback {
                activity.runOnUiThread {
                    Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                    val otherAddress = it.text
                    (activity as MainActivity?)!!.replaceFragment(BluetoothConnection())
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

}