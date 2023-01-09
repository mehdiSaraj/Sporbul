package com.mahdi.sporbul.ui.events.home


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdi.sporbul.databinding.FragmentEventsBinding
import com.mahdi.sporbul.databinding.FragmentHomeBinding
import com.mahdi.sporbul.models.EventDocument
import com.mahdi.sporbul.models.EventsFilter
import com.mahdi.sporbul.ui.events.MainActivity
import com.mahdi.sporbul.ui.events.MainActivityViewModel
import com.mahdi.sporbul.ui.events.adapters.EventsRecyclerAdapter
import com.mahdi.sporbul.ui.events.events.AddEditEventFragment
import com.mahdi.sporbul.ui.events.events.EventsFragmentViewModel
import com.mahdi.sporbul.ui.events.events.ShowEventFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: HomeFragmentViewModel by viewModels()

    private val db = Firebase.firestore

    private lateinit var eventsAdapter: EventsRecyclerAdapter
    private val eventsList = mutableListOf<EventDocument>()

    private val onItemClickListener by lazy {
        object : EventsRecyclerAdapter.RecyclerClickListener {
            override fun onItemClickListener(position: Int, event: EventDocument) {
                (activity as MainActivity).addFragment(ShowEventFragment.newInstance(event))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel.setDatabase(db)
        viewModel.fetchEvents()
        eventsAdapter = EventsRecyclerAdapter(eventsList.toMutableList(), onItemClickListener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventsAdapter
        }
        viewModel.events.observe(viewLifecycleOwner) {
            eventsList.clear()
            eventsList.addAll(it)
            eventsAdapter.setList(it)
        }
        mainViewModel.filter.observe(viewLifecycleOwner) {
            filterItemsList(it)
        }
    }

    private fun filterItemsList(filter: EventsFilter?) {
        if (filter == null) {
            eventsAdapter.setList(eventsList)
            return
        }
        val filtered = eventsList.filter { event ->
            var res = true
            if (filter.age != null) {
                res = res && event.ageIdx == filter.age
            }
            if (filter.city != null) {
                res = res && event.cityIdx == filter.city
            }
            if (filter.type != null) {
                res = res && event.typeIdx == filter.type
            }
            res
        }
        eventsAdapter.setList(filtered)
    }

}