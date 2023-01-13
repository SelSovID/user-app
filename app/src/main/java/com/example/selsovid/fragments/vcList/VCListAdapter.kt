package com.example.selsovid.fragments.vcList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.selsovid.R
import com.example.selsovid.database.VCDatabase
import com.example.selsovid.database.VcDao
import kotlinx.coroutines.flow.count

class VCListAdapter : RecyclerView.Adapter<VCListItemHolder>() {
    lateinit var database: VcDao
    lateinit var workerThread: Thread

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VCListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        workerThread = Thread()
        database = VCDatabase.getInstance(parent.context).vcDao()
        return VCListItemHolder(view)
    }

    override fun onBindViewHolder(holder: VCListItemHolder, position: Int) {
        val item = database.getAllVCs()[position]
        holder.textView.text = item.vc_name
    }

    override fun getItemCount(): Int {
        return database.getAllVCs().count()
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.frame_vclist_item
    }
}