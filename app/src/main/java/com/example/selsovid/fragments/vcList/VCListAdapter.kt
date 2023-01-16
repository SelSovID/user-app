package com.example.selsovid.fragments.vcList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.selsovid.R
import com.example.selsovid.database.VerifiableCredential

class VCListAdapter : ListAdapter<VerifiableCredential, VCListItemHolder>(VerifiableCredentialComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VCListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return VCListItemHolder(view)
    }

    override fun onBindViewHolder(holder: VCListItemHolder, position: Int) {
        val item = getItem(position)
        holder.textView.text = item.vc_request_text
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.frame_vclist_item
    }

    companion object {
        private val VerifiableCredentialComparator = object : DiffUtil.ItemCallback<VerifiableCredential>() {
            override fun areItemsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
                return oldItem.vc_name == newItem.vc_name
            }
        }
    }
}