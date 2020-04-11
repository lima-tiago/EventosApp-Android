package com.example.eventosapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventosapp.R
import com.example.eventosapp.adapter.AdapterUsers
import kotlinx.android.synthetic.main.activity_user_recycler.*

class UserRecycler : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_recycler)

        configureAdapter()
    }

    private fun configureAdapter() {
        recycler_users.adapter = AdapterUsers("hello")
        recycler_users.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}
