package com.example.selsovid.fragments.vcList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.selsovid.R
import com.example.selsovid.SSICertUtilities
import com.example.selsovid.database.VerifiableCredential
import java.util.LinkedList


class VCListAdapter : ListAdapter<VerifiableCredential, VCListAdapter.VCListItemHolder>(VerifiableCredentialComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VCListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return VCListItemHolder(view)
    }

    var holders: HashSet<VCListItemHolder> = HashSet()

    override fun onBindViewHolder(holder: VCListItemHolder, position: Int) {
        val item = getItem(position)
        val textComponents = item.text.split("\n\n")
        if (textComponents.count() > 1) {
            holder.text = textComponents[0]
        } else {
            holder.text = item.text
        }
        holder.cert = item.data
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
        val view: FrameLayout
        var text: String
            get() = view.findViewById<TextView>(R.id.vc_item_text).text.toString()
            set(value) {
                view.findViewById<TextView>(R.id.vc_item_text).text = value
            }

        var cert: String? = null

        val checked: Boolean
            get() = view.findViewById<CheckBox>(R.id.vc_item_checkbox).isChecked
        init {
            view = itemView.findViewById(R.id.vclist_item)
        }
    }
}