package com.example.selsovid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit


class SendRequest: AppCompatActivity() {
    private var dataModel: ArrayList<CustomAdapter.DataModel>? = null
    private lateinit var listView: ListView
    private lateinit var adapter: CustomAdapter

    private lateinit var requestString: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_request)

        var requestName = findViewById<EditText>(R.id.request_name)
        var sendRequestButton = findViewById<Button>(R.id.sendRequestButton)
        var backbutton = findViewById<Button>(R.id.backButton)

        backbutton.setOnClickListener{
            val intent = Intent (this, MainActivity::class.java)
            this.startActivity(intent)
        }

        sendRequestButton.setOnClickListener {
            var issuerID = 1
            //var fromUser = "ssi.is.cool@gmail.com"
            requestString = requestName.text.toString()
            var attachedVCs = arrayOf<String>()
            var apiRequester = APIRequestsUtitities.APIRequester()

            //APIRequestsUtitities.APIRequester(issuerID, fromUser, requestString, attachedVCs)
            //requestString =  1, ssi.is.cool@gmail.com, $requestString, []

            //Toast.makeText(this, requestString, Toast.LENGTH_LONG).show()
            val thread = Thread(Runnable {
                println("thread started.")
                //connectToAPI()
                apiRequester.connectToAPI(issuerID, requestString, attachedVCs)
                Thread.sleep(5000)
                println("thread done yay.")


            })
            thread.start()
            }

//        listView = findViewById<View>(R.id.listView) as ListView
//        dataModel = ArrayList<CustomAdapter.DataModel>()
//        dataModel!!.add(CustomAdapter.DataModel("Apple Pie", false))
//        dataModel!!.add(CustomAdapter.DataModel("Banana Bread", false))
//        dataModel!!.add(CustomAdapter.DataModel("Cupcake", false))
//        dataModel!!.add(CustomAdapter.DataModel("Donut", true))
//        dataModel!!.add(CustomAdapter.DataModel("Eclair", true))
//        dataModel!!.add(CustomAdapter.DataModel("Froyo", true))
//        dataModel!!.add(CustomAdapter.DataModel("Gingerbread", true))
//        dataModel!!.add(CustomAdapter.DataModel("Honeycomb", false))
//
//        adapter = CustomAdapter(dataModel!!, applicationContext)
//        listView.adapter = adapter
//
//        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            val dataModel: CustomAdapter.DataModel = dataModel!![position]
//            dataModel.checked = !dataModel.checked
//            adapter.notifyDataSetChanged()
//        }


    }



}