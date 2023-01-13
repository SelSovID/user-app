package com.example.selsovid.fragments.vcList

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.selsovid.R

/**
 * A fragment representing a list of Items.
 */
class VCList : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vclist, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.vclist)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(view.context)
        recycler.adapter = VCListAdapter()

        return view
    }
}