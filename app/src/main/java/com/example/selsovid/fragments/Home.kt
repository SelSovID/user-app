package com.example.selsovid.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.selsovid.R
import com.example.selsovid.QRCode
import com.example.selsovid.SendRequest



class Home : Fragment() {

    lateinit var mView: View
    lateinit var requestString: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        val generateButton = mView.findViewById(R.id.btnGenerate) as? Button
        val requestButton = mView.findViewById(R.id.gotoRequest) as? Button
        var vcToShare = mView.findViewById<EditText>(R.id.vcToShare)

        generateButton?.setOnClickListener{
            val intent = Intent (getActivity(), QRCode::class.java)
            getActivity()?.startActivity(intent)
            requestString = vcToShare.text.toString()

        }

        requestButton?.setOnClickListener{
            val intent = Intent (getActivity(), SendRequest::class.java)
            getActivity()?.startActivity(intent)
        }

        return mView
    }

}