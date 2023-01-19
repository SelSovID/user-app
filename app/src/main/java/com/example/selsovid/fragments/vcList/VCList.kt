package com.example.selsovid.fragments.vcList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import com.example.selsovid.R
import com.example.selsovid.activities.AddVC
import com.example.selsovid.activities.ShareVC
import com.example.selsovid.database.VCDatabase
import com.example.selsovid.databinding.FragmentVclistBinding
import com.example.selsovid.models.parcels.AttachedVCs
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A fragment representing a list of Items.
 */
class VCList : Fragment() {

    private lateinit var binding: FragmentVclistBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVclistBinding.inflate(inflater)
        val view = inflater.inflate(R.layout.fragment_vclist, container, false)
        val database = VCDatabase.getInstance(view.context).vcDao()

        val recycler = view.findViewById<RecyclerView>(R.id.vclist_recyclerView)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(view.context)
        val adapter = VCListAdapter()
        recycler.adapter = adapter
        database.getAllVCs().asLiveData().observe(viewLifecycleOwner) { words ->
            words.let {
                adapter.submitList(it)
            }
        }
        view.findViewById<FloatingActionButton>(R.id.add_vc_button)
            .setOnClickListener {
                val checked = adapter.getChecked()
                val attached = AttachedVCs(checked.map {it.export()})
                val intent = Intent(activity, AddVC::class.java)
                intent.putExtra("attachedVCs", attached)
                startActivity(intent)
                false
            }
        view.findViewById<FloatingActionButton>(R.id.share_vc_button)
            .setOnClickListener {
                val checked = adapter.getChecked()
                val attached = AttachedVCs(checked.map {it.export()})
                val intent = Intent(activity, ShareVC::class.java)
                intent.putExtra("attachedVCs", attached)
                startActivity(intent)
                false
            }


        return view
    }
}

