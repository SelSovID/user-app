package com.example.selsovid

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.selsovid.database.DBKeyPair
import com.example.selsovid.database.VCDatabase
import com.example.selsovid.database.VerifiableCredential
import com.example.selsovid.fragments.vcList.VCList
import com.example.selsovid.jsonModel.PostVCRequest
import com.example.selsovid.jsonModel.PostVCResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit


class SendRequest: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_request)
        supportFragmentManager.commit {
            replace<VCList>(R.id.vc_list_fragment_container)
        }

        val credentialTextHolder = findViewById<EditText>(R.id.credential_text)
        val sendRequestButton = findViewById<Button>(R.id.sendRequestButton)
        val backButton = findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener{
            val intent = Intent (this, MainActivity::class.java)
            this.startActivity(intent)
        }

        sendRequestButton.setOnClickListener {
            val credentialText = credentialTextHolder.text.toString()

//            Toast.makeText(this, credentialText, Toast.LENGTH_LONG).show()
            val thread = Thread {
                ensureKeyPairExists()
                val cert = SSICertUtilities.create(publicKey, privateKey, credentialText)
                val retrievalId = postRequest(cert)
                val vcDao = VCDatabase.getInstance(applicationContext).vcDao()
                val vc = VerifiableCredential(null, cert.export(), retrievalId, cert.credentialText)
                vcDao.insert(vc)
            }
            thread.start()
            }


    }

    private fun ensureKeyPairExists() {
        val database = VCDatabase.getInstance(applicationContext).kpDao()
        if (database.getPair() == null) {
            val kpg = KeyPairGenerator.getInstance("RSA")
            kpg.initialize(2048)
            val pair = kpg.generateKeyPair()
            val dbPair = DBKeyPair(null, pair.public.encoded, pair.private.encoded)
            database.createPair(dbPair)
        }
    }

    private val publicKey: RSAPublicKey
        get() {
            val database = VCDatabase.getInstance(applicationContext).kpDao()
            val pair = database.getPair()
            if (pair != null) {
                return KeyFactory
                    .getInstance("RSA").generatePublic(
                        X509EncodedKeySpec(pair.publicKey)
                    ) as RSAPublicKey
            } else {
                throw IOException("Cannot retrieve non-existing key")
            }
        }

    private val privateKey: RSAPrivateKey
        get() {
            val database = VCDatabase.getInstance(applicationContext).kpDao()
            val pair = database.getPair()
            if (pair != null) {
                return KeyFactory
                    .getInstance("RSA").generatePrivate(
                        PKCS8EncodedKeySpec(pair.privateKey)
                    ) as RSAPrivateKey
            } else {
                throw IOException("Cannot retrieve non-existing key")
            }
        }
    companion object {
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()


        private fun postRequest(vc: SSICertUtilities): String {
            val postBody = Json.encodeToString(PostVCRequest(vc.export(), arrayOf(), 1))
            val client = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder()
                .url("https://ssi.s.mees.io/api/holder/request")
                .post(postBody.toRequestBody(MEDIA_TYPE_JSON))
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response, request: $postBody")

                val responseBody = response.body!!.string()
                val responsePayload = Json.decodeFromString<PostVCResponse>(responseBody)

                return responsePayload.id
            }
        }
    }


}
