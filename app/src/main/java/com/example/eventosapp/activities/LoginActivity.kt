package com.example.eventosapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventosapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        do_not_have_account_text_view.setOnClickListener {
            val i = Intent(this,Register::class.java)
            startActivity(i)
        }

        login_button.setOnClickListener {
            val i = Intent(this,Menu::class.java)
            startActivity(i)
        }
    }
}
