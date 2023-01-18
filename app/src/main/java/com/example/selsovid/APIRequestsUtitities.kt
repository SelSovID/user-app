package com.example.selsovid

import android.app.Activity
import android.os.Bundle
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocketListener
import java.io.IOException
import java.util.concurrent.TimeUnit

class APIRequestsUtitities {
    @Serializable
    data class APIMessage(var issuerId: Int, var requestString: String, val attachedVCs: Array<String>)

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    class APIRequester(): Activity(){

        fun connectToAPI(issuerId: Int, requestString: String, attachedVCs: Array<String>) {
            //Log.v("apirequester", "running in apirequester")
            var stringOfRequest = Json.encodeToString(requestString)
            var postBody = APIMessage(issuerId, stringOfRequest.trimMargin(), attachedVCs).toString().trimMargin()
            Log.v("apirequester", postBody)
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

}