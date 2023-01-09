package com.mahdi.sporbul.ui.events.events

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.R
import com.mahdi.sporbul.databinding.FragmentAddEventBinding
import com.mahdi.sporbul.models.Event
import com.mahdi.sporbul.models.EventDocument
import com.mahdi.sporbul.models.User
import com.mahdi.sporbul.ui.account.showToast
import com.mahdi.sporbul.ui.events.MainActivity
import com.mahdi.sporbul.utils.toDateString
import com.mahdi.sporbul.utils.toTimeString
import java.util.Calendar


class AddEditEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding: FragmentAddEventBinding get() = _binding!!

    private val viewModel: AddEditEventViewModel by viewModels()
    private val db = Firebase.firestore
    private lateinit var user: User

    var dateMillis: Long? = 0L
    var hour: Int? = null
    var minute: Int? = null

    var event: EventDocument? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        event = arguments?.getSerializable("event") as? EventDocument
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        viewModel.setDatabase(db)
        user = AppSharedPrefs.getUser()!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        event?.let {
            binding.apply {
                nameEditText.setText(it.name)
                typeAutoComplete.setText(getStringAtIndex(R.array.sport_type, it.typeIdx), false)
                ageAutoComplete.setText(getStringAtIndex(R.array.age_ranges, it.ageIdx), false)
                dateEditText.setText(it.time.toDateString())
                timeEditText.setText(it.time.toTimeString())
                cityAutoComplete.setText(getStringAtIndex(R.array.turkey_city, it.cityIdx), false)
                addressEditText.setText(it.address)
                notesEditText.setText(it.notes)
                phoneEditText.setText(it.contactNumber)
                submitBtn.text = getString(R.string.update)
            }
            val calendar = Calendar.getInstance().apply {
                timeInMillis = it.time
            }
            dateMillis = calendar.timeInMillis
            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)
        }
        binding.dateEditText.setOnClickListener {
            openDatePicker()
        }
        binding.timeEditText.setOnClickListener {
            openTimePicker()
        }
        binding.submitBtn.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun getStringAtIndex(arrayId: Int, typeIdx: Int): String {
        return try {
            resources.getStringArray(arrayId)[typeIdx]
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    private fun validateAndSubmit() {
        var foundErrors = false
        binding.root.children.iterator().forEach { view ->
            if (view is TextInputLayout) {
                if (view.editText?.text?.isBlank() == true) {
                    foundErrors = true
                    view.editText?.error = "This field is required!"
                } else {
                    view.editText?.error = null
                }
            }
        }

        val ageIdx = getIndex(R.array.age_ranges, binding.ageAutoComplete.text.toString())
        val typeIdx = getIndex(R.array.sport_type, binding.typeAutoComplete.text.toString())
        val cityIdx = getIndex(R.array.turkey_city, binding.cityAutoComplete.text.toString())

        if (foundErrors || ageIdx < 0 || typeIdx < 0 || cityIdx < 0 || hour == null || minute == null || dateMillis == null) {
            requireContext().showToast("Please enter valid data")
            return
        }

        val timeMillis = getTimeMillis()

        binding.apply {
            val newEvent = Event(
                authorEmail = user.email,
                name = nameEditText.text.toString(),
                typeIdx = typeIdx,
                ageIdx = ageIdx,
                cityIdx = cityIdx,
                time = timeMillis,
                address = addressEditText.text.toString(),
                contactNumber = phoneEditText.text.toString(),
                notes = notesEditText.text.toString()
            )

            if (event != null) {
                viewModel.updateEvent(event!!.id, newEvent)
            } else {
                viewModel.addEvent(newEvent)
            }
        }

        (activity as MainActivity).goBack()
    }

    private fun getTimeMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis!!
        calendar.set(Calendar.HOUR_OF_DAY, hour!!)
        calendar.set(Calendar.MINUTE, minute!!)
        return calendar.timeInMillis
    }

    private fun openDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraints)
            .build()
        datePicker.show(childFragmentManager, this.tag)
        datePicker.addOnPositiveButtonClickListener { dateMillis ->
            this.dateMillis = dateMillis
            binding.dateEditText.setText(dateMillis.toDateString())
        }
    }

    private fun openTimePicker() {
        val calendar = Calendar.getInstance()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(getString(R.string.select_time))
            .build()
        picker.show(childFragmentManager, this.tag)
        picker.addOnPositiveButtonClickListener {
            this.hour = picker.hour
            this.minute = picker.minute
            binding.timeEditText.setText("${picker.hour}:${String.format("%02d", picker.minute)}")
        }
    }

    fun getIndex(arrayId: Int, string: String): Int {
        val array = resources.getStringArray(arrayId)
        return array.indexOf(string)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}