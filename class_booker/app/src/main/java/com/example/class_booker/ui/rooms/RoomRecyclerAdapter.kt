package com.example.class_booker.ui.rooms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.class_booker.R
import com.example.class_booker.data.model.Room

class RoomRecyclerAdapter(
    private val onClick: ((Room) -> Unit)?
) : ListAdapter<Room, RoomRecyclerAdapter.ContactViewHolder>(ContactDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.room_item_list, parent, false)
        return ContactViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
    }

    class ContactViewHolder(
        itemView: View,
        private val onClick: ((Room) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {

        private var current: Room? = null

        init {
            itemView.setOnClickListener {
                current?.let { onClick?.invoke(it) }
            }
        }

        fun bind(room: Room) {
            current = room

            itemView.findViewById<TextView>(R.id.name).text = room.name
            itemView.findViewById<TextView>(R.id.description).text = room.description
            itemView.findViewById<TextView>(R.id.capacity).text = room.capacity.toString()
        }
    }
}

object ContactDiffCallback : DiffUtil.ItemCallback<Room>() {
    override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
        return oldItem.id == newItem.id
    }
}