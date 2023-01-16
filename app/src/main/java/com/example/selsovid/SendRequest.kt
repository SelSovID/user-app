package com.example.selsovid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit


class SendRequest: Activity() {
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
            requestString = requestName.text.toString()
            requestString = "{" +
                    "\"issuerId\": 1, " +
                    "\"fromUser\": \"ssi.is.cool@gmail.com\", " +
                    "\"requestText\": \"$requestString\", " +
                    "\"attachedVCs\": []" +
                    "}"
            Toast.makeText(this, requestString, Toast.LENGTH_LONG).show()
            val thread = Thread(Runnable {
                println("thread started.")
                connectToAPI()
                Thread.sleep(5000)
                println("thread done yay.")


            })
            thread.start()
            }

        listView = findViewById<View>(R.id.listView) as ListView
        dataModel = ArrayList<CustomAdapter.DataModel>()
        dataModel!!.add(CustomAdapter.DataModel("Apple Pie", false))
        dataModel!!.add(CustomAdapter.DataModel("Banana Bread", false))
        dataModel!!.add(CustomAdapter.DataModel("Cupcake", false))
        dataModel!!.add(CustomAdapter.DataModel("Donut", true))
        dataModel!!.add(CustomAdapter.DataModel("Eclair", true))
        dataModel!!.add(CustomAdapter.DataModel("Froyo", true))
        dataModel!!.add(CustomAdapter.DataModel("Gingerbread", true))
        dataModel!!.add(CustomAdapter.DataModel("Honeycomb", false))

        adapter = CustomAdapter(dataModel!!, applicationContext)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val dataModel: CustomAdapter.DataModel = dataModel!![position]
            dataModel.checked = !dataModel.checked
            adapter.notifyDataSetChanged()
        }


    }
    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    fun connectToAPI(){
        val postBody = requestString.trimMargin()
        val client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("https://ssi.s.mees.io/api/holder/request")
            .post(postBody.toRequestBody(MEDIA_TYPE_JSON))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response, request: $postBody")

            println(response.body!!.string())
        }


    }




}