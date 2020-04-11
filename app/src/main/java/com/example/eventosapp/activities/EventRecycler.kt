package com.example.eventosapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventosapp.R
import com.example.eventosapp.adapter.AdapterEvents
import kotlinx.android.synthetic.main.activity_event_recycler.*

class EventRecycler : AppCompatActivity() {

    var lista_eventos: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_recycler)

        lista_eventos.add("Evento")
        configureAdapter()
    }

    private fun configureAdapter() {
        recycler_eventos.adapter = AdapterEvents(lista_eventos,this)
        recycler_eventos.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    //    temporario
    fun addEvento() {
        lista_eventos.add("Evento")
        configureAdapter()
    }
}
