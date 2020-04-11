package com.example.eventosapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventosapp.R
import kotlinx.android.synthetic.main.activity_menu.*

class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        event_list_button.setOnClickListener {
            val i = Intent(this,EventRecycler::class.java)
            startActivity(i)
        }

        user_list_button.setOnClickListener {
            val i = Intent(this,UserRecycler::class.java)
            startActivity(i)
        }
    }
}
