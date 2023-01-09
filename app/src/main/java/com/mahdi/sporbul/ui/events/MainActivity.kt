package com.mahdi.sporbul.ui.events

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.R
import com.mahdi.sporbul.databinding.ActivityMainBinding
import com.mahdi.sporbul.models.EventsFilter
import com.mahdi.sporbul.models.User
import com.mahdi.sporbul.ui.events.events.EventsFragment
import com.mahdi.sporbul.ui.events.home.HomeFragment
import com.mahdi.sporbul.ui.events.map.MapFragment
import com.mahdi.sporbul.ui.events.settings.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var homeFragment: HomeFragment
    private var mapFragment: MapFragment? = null
    private var eventsFragment: EventsFragment? = null
    private var settingsFragment: SettingsFragment? = null
    private lateinit var user: User
    private var hideFilter = false

    private val db = Firebase.firestore
    private var optionsMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = AppSharedPrefs.getUser()!!
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNav.menu.findItem(R.id.myEvents).isVisible = user.isAdmin
        setContentView(binding.root)

        viewModel.setDatabase(db)
        homeFragment = HomeFragment()

        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, homeFragment, homeFragment.tag)
            .commit()

        setupBottomNav()

        viewModel.userData.observe(this) {
            updateUser(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        optionsMenu = menu

        if (hideFilter) {
            optionsMenu?.findItem(R.id.filter)?.isVisible = false
        }

        menu.findItem(R.id.filter).setOnMenuItemClickListener {
            openFiltersSheet()
            true
        }

        return true
    }

    private fun openFiltersSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.filter_layout)

        val typeEt = bottomSheetDialog.findViewById<AutoCompleteTextView>(R.id.typeAutoComplete)
        val cityEt = bottomSheetDialog.findViewById<AutoCompleteTextView>(R.id.cityAutoComplete)
        val ageEt = bottomSheetDialog.findViewById<AutoCompleteTextView>(R.id.ageAutoComplete)

        val applyBtn = bottomSheetDialog.findViewById<MaterialButton>(R.id.applyBtn)
        val resetBtn = bottomSheetDialog.findViewById<TextView>(R.id.resetBtn)

        viewModel.filter.value?.let { filter ->
            if (filter.type != null) {
                typeEt?.setText(getStringAtIndex(R.array.sport_type, filter.type), false)
            }
            if (filter.age != null) {
                ageEt?.setText(getStringAtIndex(R.array.age_ranges, filter.age), false)
            }
            if (filter.city != null) {
                cityEt?.setText(getStringAtIndex(R.array.turkey_city, filter.city), false)
            }
        }

        applyBtn?.setOnClickListener {
            val typeIdx = getIndex(R.array.sport_type, typeEt!!.text.toString())
            val ageIdx = getIndex(R.array.age_ranges, ageEt!!.text.toString())
            val cityId = getIndex(R.array.turkey_city, cityEt!!.text.toString())
            val filter = EventsFilter(
                type = if (typeIdx != -1) typeIdx else null,
                age = if (ageIdx != -1) ageIdx else null,
                city = if (cityId != -1) cityId else null,
            )
            viewModel.setFilter(filter)
            bottomSheetDialog.dismiss()
        }

        resetBtn?.setOnClickListener {
            viewModel.setFilter(null)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showFiltersOption() {
        hideFilter = false
        this.invalidateOptionsMenu()
    }

    private fun hideFiltersOption() {
        hideFilter = true
        this.invalidateOptionsMenu()
    }

    private fun updateUser(newUser: User) {
        user = newUser
        binding.bottomNav.menu.findItem(R.id.myEvents).isVisible = user.isAdmin
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchUserData()
    }

    private fun getIndex(arrayId: Int, string: String): Int {
        val array = resources.getStringArray(arrayId)
        return array.indexOf(string)
    }

    private fun getStringAtIndex(arrayId: Int, typeIdx: Int): String {
        return try {
            resources.getStringArray(arrayId)[typeIdx]
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            var newFrag: Fragment? = null
            when (item.itemId) {
                R.id.home -> {
                    newFrag = homeFragment
                    showFiltersOption()
                }
//                R.id.map -> {
//                    if (mapFragment == null) {
//                        mapFragment = MapFragment()
//                    }
//                    newFrag = mapFragment
//                }
                R.id.myEvents -> {
                    if (eventsFragment == null) {
                        eventsFragment = EventsFragment()
                    }
                    newFrag = eventsFragment
                    hideFiltersOption()
                }
                R.id.settings -> {
                    if (settingsFragment == null) {
                        settingsFragment = SettingsFragment()
                    }
                    newFrag = settingsFragment
                    hideFiltersOption()
                }
            }
            if (newFrag != null) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.container.id, newFrag, newFrag.tag)
                    .commit()
            }
            true
        }
    }

    fun addFragment(fragment: Fragment) {
        val fragmentToOpen = supportFragmentManager.findFragmentByTag(fragment.tag)
        if (fragmentToOpen == null) {
            binding.bottomNav.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .add(binding.container.id, fragment, fragment.tag)
                .addToBackStack(fragment.tag)
                .commit()
        }
    }

    fun goBack() = onBackPressed()

    override fun onBackPressed() { // we only have one layer above
        super.onBackPressed()
        binding.bottomNav.visibility = View.VISIBLE
    }

}