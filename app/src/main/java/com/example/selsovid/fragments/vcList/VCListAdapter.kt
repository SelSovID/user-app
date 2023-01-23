package com.example.selsovid.fragments.vcList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.selsovid.R
import com.example.selsovid.SSICertUtilities
import com.example.selsovid.activities.MainActivity
import com.example.selsovid.database.VCDatabase
import com.example.selsovid.database.VcDao
import com.example.selsovid.database.VerifiableCredential
import com.example.selsovid.models.API.RetrieveVCResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.LinkedList
import java.util.concurrent.TimeUnit


class VCListAdapter(private val activity: FragmentActivity) : ListAdapter<VerifiableCredential, VCListAdapter.VCListItemHolder>(VerifiableCredentialComparator) {
    private var vcDao: VcDao? = null
    private var appContext: Context? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VCListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        vcDao = VCDatabase.getInstance(parent.context).vcDao()
        appContext = parent.context;
        return VCListItemHolder(view)
    }

    var holders: HashSet<VCListItemHolder> = HashSet()

    override fun onBindViewHolder(holder: VCListItemHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, vcDao!!, this)
        holders.add(holder)
    }

    fun getChecked(): List<SSICertUtilities> {
        var res = LinkedList<SSICertUtilities>()
        for (holder in holders.filter {
            it.checked
        }) {
            res.add(SSICertUtilities.import(holder.cert!!))
        }
        return res
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.frame_vclist_item
    }

    companion object {
        private val VerifiableCredentialComparator = object : DiffUtil.ItemCallback<VerifiableCredential>() {
            override fun areItemsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
                return oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
                return oldItem.text == newItem.text
            }
        }
    }

    class VCListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var adapter: VCListAdapter? = null
        val view: FrameLayout
        var text: String
            get() = view.findViewById<TextView>(R.id.vc_item_text).text.toString()
            set(value) {
                view.findViewById<TextView>(R.id.vc_item_text).text = value
            }


        private var vc: VerifiableCredential? = null
        private val retrievalId: String?
            get() = vc?.requestId

        val cert: String?
            get() = vc?.data

        private var vcDao: VcDao? = null

        val checked: Boolean
            get() = view.findViewById<CheckBox>(R.id.vc_item_checkbox).isChecked
        init {
            view = itemView.findViewById(R.id.vclist_item)
        }

        fun bind(item: VerifiableCredential, vcDao: VcDao, adapter: VCListAdapter) {
            this.adapter = adapter
            vc = item
            this.vcDao = vcDao
            val textComponents = item.text.split("\n\n")
            if (textComponents.count() > 1) {
                text = textComponents[0]
            } else {
                text = item.text
            }

            val checkBox = view.findViewById<CheckBox>(R.id.vc_item_checkbox)
            val button = view.findViewById<Button>(R.id.vc_item_check_button)
            if (item.requestId != null) {

                checkBox.visibility = INVISIBLE
                checkBox.isChecked = false

                button.visibility = VISIBLE
                button.setOnClickListener {
                    Thread {
                        checkVCStatus()
                    }.start()
                }
            } else {
                checkBox.visibility = VISIBLE
                button.visibility = INVISIBLE
            }
        }

        private fun checkVCStatus() {

            val client = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder()
                .url("https://ssi.s.mees.io/api/holder/request/${retrievalId}")
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                if (response.code == 200) {
                    val res = Json.decodeFromString<RetrieveVCResponse>(response.body!!.string())
                    if (res.accept) {
                        val newVC = VerifiableCredential(vc!!.id, res.vc!!, null, vc!!.text)
                        vcDao!!.update(newVC)
                        adapter!!.activity.runOnUiThread {
                            makeText(
                                adapter!!.appContext,
                                "Je VC is opgehaald!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        vcDao!!.delete(vc!!)
                        if (res.denyReason != null && res.denyReason != "") {
                            adapter!!.activity.runOnUiThread {
                                makeText(
                                    adapter!!.appContext,
                                    res.denyReason,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            adapter!!.activity.runOnUiThread {
                                makeText(
                                    adapter!!.appContext,
                                    "Je VC is helaas afgekeurd",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    val intent = Intent(view.context, MainActivity::class.java)
                    intent.flags =FLAG_ACTIVITY_NEW_TASK
                    view.context.applicationContext.startActivity(intent)
                } else {
                    adapter!!.activity.runOnUiThread {
                        makeText(
                            adapter!!.appContext,
                            "Er is nog niet gereageerd op je verzoek",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}