package com.mahdi.sporbul.ui.events.settings

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mahdi.sporbul.R

class SettingsItem constructor(
    context: Context, attrs: AttributeSet
) : LinearLayout(context, attrs) {

    private val root: View = inflate(context, R.layout.item_settings, this)
    private val title: TextView = findViewById(R.id.title)
    private val description: TextView = findViewById(R.id.description)
    private val arrow: ImageView = findViewById(R.id.arrow)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem)
        title.text = attributes.getString(R.styleable.SettingsItem_title)
        description.text = attributes.getString(R.styleable.SettingsItem_description)
        val showArrow = attributes.getBoolean(R.styleable.SettingsItem_clickableItem, false)
        if (description.text.isBlank()) {
            description.visibility = View.GONE
        }
        if (showArrow) {
            arrow.visibility = View.VISIBLE
            this.isClickable = true
            this.isFocusable = true
        }
        attributes.recycle()
    }

    fun setDescription(value: String) {
        description.text = value
        if (description.text.isBlank()) {
            description.visibility = View.GONE
        }
    }
}