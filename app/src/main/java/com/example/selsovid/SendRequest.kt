package com.example.selsovid

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
        setContentView(R.layout.fragment_send_request)

        var requestName = findViewById<EditText>(R.id.request_name)
        var sendRequestButton = findViewById<Button>(R.id.sendRequestButton)

        sendRequestButton.setOnClickListener {
            requestString = requestName.text.toString()
            Toast.makeText(this, requestString, Toast.LENGTH_LONG).show()
            connectToAPI()
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
            val dataModel: CustomAdapter.DataModel = dataModel!![position] as CustomAdapter.DataModel
            dataModel.checked = !dataModel.checked
            adapter.notifyDataSetChanged()
        }


    }
    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
    }

    fun connectToAPI(){
        val postBody = requestString.trimMargin()
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("wss://ssi.s.mees.io/api")
            .post(postBody.toRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            println(response.body!!.string())
        }


    }




}