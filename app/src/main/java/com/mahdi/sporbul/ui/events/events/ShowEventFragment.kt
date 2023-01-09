package com.mahdi.sporbul.ui.events.events

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mahdi.sporbul.R
import com.mahdi.sporbul.databinding.FragmentShowEventBinding
import com.mahdi.sporbul.models.EventDocument
import com.mahdi.sporbul.utils.toDateTimeString


private const val ARG_EVENT = "event"

class ShowEventFragment : Fragment() {

    private var event: EventDocument? = null

    private var _binding: FragmentShowEventBinding? = null
    private val binding: FragmentShowEventBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getSerializable(ARG_EVENT) as EventDocument
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            title.text = event!!.name
            age.text = getStringAtIndex(R.array.age_ranges, event!!.ageIdx)
            eventType.text = getStringAtIndex(R.array.sport_type, event!!.typeIdx)
            notes.text = event!!.notes
            number.text = event!!.contactNumber
            dateTime.text = event!!.time.toDateTimeString()
            location.text =
                "${getStringAtIndex(R.array.turkey_city, event!!.cityIdx)}, ${event!!.address}"
            image.setImageResource(getImageResource(event!!.typeIdx))
            number.setOnClickListener { openDialNumber(event!!.contactNumber) }
        }
    }

    private fun openDialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        startActivity(intent)
    }

    private fun getImageResource(typeIdx: Int): Int {
        return when (typeIdx) {
            0 -> R.drawable.football_bg
            1 -> R.drawable.basketball_bg
            2 -> R.drawable.volley_bg
            3 -> R.drawable.tennis_bg
            else -> R.drawable.logo
        }
    }

    private fun getStringAtIndex(arrayId: Int, typeIdx: Int): String {
        return try {
            resources.getStringArray(arrayId)[typeIdx]
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(event: EventDocument) =
            ShowEventFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_EVENT, event)
                }
            }
    }
}