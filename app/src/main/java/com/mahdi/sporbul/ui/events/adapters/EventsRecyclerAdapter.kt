package com.mahdi.sporbul.ui.events.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mahdi.sporbul.R
import com.mahdi.sporbul.databinding.ItemEventBinding
import com.mahdi.sporbul.models.EventDocument

class EventsRecyclerAdapter(
    private val itemsList: MutableList<EventDocument>,
    private val listener: RecyclerClickListener
) : RecyclerView.Adapter<EventsRecyclerAdapter.EventItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventItemViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: EventItemViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int = itemsList.size

    fun setList(list: List<EventDocument>) {
        this.itemsList.clear()
        this.itemsList.addAll(list)
        notifyDataSetChanged()
    }


    class EventItemViewHolder(
        private val binding: ItemEventBinding,
        private val listener: RecyclerClickListener
    ) : ViewHolder(binding.root) {
        fun bind(event: EventDocument) {
            binding.title.text = event.name
            binding.desc.text = getStringAtIndex(R.array.sport_type, event.typeIdx)
            binding.icon.setImageResource(getIcon(event))
            binding.root.setOnClickListener {
                listener.onItemClickListener(adapterPosition, event)
            }
        }

        private fun getIcon(event: EventDocument): Int {
            return when(event.typeIdx){
                0 -> R.drawable.football
                1 -> R.drawable.basketball
                2 -> R.drawable.volley
                3 -> R.drawable.ping_pong
                else -> R.drawable.logo
            }
        }

        private fun getStringAtIndex(arrayId: Int, typeIdx: Int): String {
            return try {
                binding.root.context.resources.getStringArray(arrayId)[typeIdx]
            } catch (ex: java.lang.Exception) {
                ""
            }
        }
    }

    interface RecyclerClickListener {
        fun onItemClickListener(position: Int, event: EventDocument)
    }
}