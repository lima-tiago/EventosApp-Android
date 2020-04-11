package com.example.eventosapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventosapp.R
import com.example.eventosapp.activities.EventDetail
import com.example.eventosapp.activities.EventRecycler
import com.example.eventosapp.activities.NewEvent
import kotlinx.android.synthetic.main.cell_new_event.view.*


class AdapterEvents(val lista_eventos: List<String>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val view_event = 1
    val view_new_event = 2
    val size_plus_one = lista_eventos.size + 1

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindEvento(nome: String, position: Int) {
            itemView.findViewById<TextView>(R.id.nome_evento_text_view).text =
                nome + position.toString()
        }
    }

    class AddEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindNewEvento(nome: String) {

        }
    }

    override fun getItemViewType(position: Int): Int {
        println("debug: position $position")
        return if (position < lista_eventos.size) {
            view_event
        } else {
            view_new_event
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mLayout = LayoutInflater.from(parent.context)

        return if (viewType == view_event) {
            EventoViewHolder(mLayout.inflate(R.layout.cell_evento, parent, false))
        } else {
            AddEventViewHolder(mLayout.inflate(R.layout.cell_new_event, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventoViewHolder) {
            holder.bindEvento(lista_eventos[position], position)
            holder.itemView.setOnClickListener {
                val i = Intent(holder.itemView.context, EventDetail::class.java)
                holder.itemView.context.startActivity(i)
            }
        } else if (holder is AddEventViewHolder) {
//            holder.bindNewEvento(lista_eventos[position])
            holder.itemView.new_event_button.setOnClickListener {
//                (context as EventRecycler).addEvento()
                val i = Intent(holder.itemView.context, NewEvent::class.java)
                holder.itemView.context.startActivity(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return size_plus_one
    }
}

