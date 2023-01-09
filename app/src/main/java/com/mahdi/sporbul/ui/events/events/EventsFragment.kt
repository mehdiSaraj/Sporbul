package com.mahdi.sporbul.ui.events.events

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdi.sporbul.databinding.FragmentEventsBinding
import com.mahdi.sporbul.models.EventDocument
import com.mahdi.sporbul.ui.events.MainActivity
import com.mahdi.sporbul.ui.events.adapters.EventsRecyclerAdapter


class EventsFragment : Fragment() {

    val viewModel: EventsFragmentViewModel by viewModels()

    private var _binding: FragmentEventsBinding? = null
    private val binding: FragmentEventsBinding get() = _binding!!

    private val db = Firebase.firestore

    private lateinit var eventsAdapter: EventsRecyclerAdapter
    private val eventsList = mutableListOf<EventDocument>()

    private val onItemClickListener by lazy {
        object : EventsRecyclerAdapter.RecyclerClickListener {
            override fun onItemClickListener(position: Int, event: EventDocument) {
                (activity as MainActivity).addFragment(
                    AddEditEventFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable("event", event)
                        }
                    }
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        viewModel.setDatabase(db)
        viewModel.fetchEvents()
        eventsAdapter = EventsRecyclerAdapter(eventsList, onItemClickListener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addBtn.setOnClickListener {
            (activity as MainActivity).addFragment(AddEditEventFragment())
        }
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventsAdapter
        }
        viewModel.events.observe(viewLifecycleOwner) {
            eventsAdapter.setList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}