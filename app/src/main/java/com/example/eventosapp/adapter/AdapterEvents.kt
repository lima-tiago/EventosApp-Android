package com.example.eventosapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventosapp.Event
import com.example.eventosapp.R
import com.example.eventosapp.activities.EventDetail
import com.example.eventosapp.activities.EventRecycler


class AdapterEvents(val event_list: MutableList<Event>, val context: Context) :
    RecyclerView.Adapter<AdapterEvents.EventoViewHolder>() {

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindEvento(event: Event) {
            itemView.findViewById<TextView>(R.id.event_name_text_view_cell).text = event.name
            itemView.findViewById<TextView>(R.id.event_creator_text_view_cell).text =
                event.creatorUsername
            itemView.findViewById<TextView>(R.id.event_location_text_view_cell).text =
                event.location
            itemView.findViewById<TextView>(R.id.event_date_text_view_cell).text = event.date
            itemView.findViewById<TextView>(R.id.event_time_text_view_cell).text =
                "Das ${event.time}"

            Glide.with(itemView).load(event.eventImageUrl)
                .into(itemView.findViewById(R.id.event_image_cell))
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_event, parent, false)
        return EventoViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return event_list.size
    }


    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        holder.bindEvento(event_list[position])
        holder.itemView.setOnClickListener {
            val i = Intent(holder.itemView.context, EventDetail::class.java)
            i.putExtra("event", event_list[position])
            holder.itemView.context.startActivity(i)

        }
    }
}

