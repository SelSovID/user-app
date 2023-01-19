package com.example.selsovid.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.selsovid.SSICertUtilities
import com.example.selsovid.database.DBKeyPair
import com.example.selsovid.database.VCDatabase
import com.example.selsovid.database.VerifiableCredential
import com.example.selsovid.databinding.ActivityAddVcBinding
import com.example.selsovid.models.parcels.AttachedVCs
import com.example.selsovid.models.API.PostVCRequest
import com.example.selsovid.models.API.PostVCResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
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

class AddVC : AppCompatActivity() {
    private lateinit var binding: ActivityAddVcBinding

    private lateinit var attachedVCs: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddVcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachedVCs = intent.getParcelableExtra("attachedVCs", AttachedVCs::class.java)?.data!!

        binding.addVcSendButton.setOnClickListener {
            val credentialText = "${binding.addVcVcTitle.text}\n\n${binding.addVcVcBody.text}"

            val thread = Thread {
                ensureKeyPairExists()
                val cert = SSICertUtilities.create(publicKey, privateKey, credentialText)
                try {
                    val retrievalId = postRequest(cert, attachedVCs)
                    val vcDao = VCDatabase.getInstance(applicationContext).vcDao()
                    val vc =
                        VerifiableCredential(null, cert.export(), retrievalId, cert.credentialText)
                    vcDao.insert(vc)

                }
                catch (e: java.lang.Exception) {
//                    c
                }
                finally {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            thread.start()
        }
    }

    private fun ensureKeyPairExists() {
        val database = VCDatabase.getInstance(applicationContext).kpDao()
        if (database.getPair() == null) {
            val kpg = KeyPairGenerator.getInstance("RSA", )
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


        private fun postRequest(vc: SSICertUtilities, attachedVCs: List<String>): String {
            val postBody = Json.encodeToString(PostVCRequest(vc.export(), attachedVCs, 1))
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