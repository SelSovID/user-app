package com.example.selsovid.fragments.vcList

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.selsovid.R

class VCListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView
    init {
        textView = itemView.findViewById(R.id.vclist_item)
    }
}