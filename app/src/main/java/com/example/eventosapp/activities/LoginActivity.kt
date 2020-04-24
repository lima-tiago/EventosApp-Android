package com.example.eventosapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventosapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "Login"
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val userUid = auth.currentUser!!.uid
    private val userRef = rootRef.child("users").child(userUid)
    var username = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        do_not_have_account_text_view.setOnClickListener {
            val i = Intent(this, Register::class.java)
            startActivity(i)
        }

        login_button.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        progress_bar_login.visibility = View.VISIBLE

        val user = email_login_edittext.text.toString()
        val password = password_login_edittext.text.toString()
//        val user = "tiago@gmail.com"
//        val password = "123123"

        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Porfavor, preencha todos os campos de usuario e senha,",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.signInWithEmailAndPassword(user, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                updateUI(auth.currentUser)
                progress_bar_login.visibility = View.GONE
            }.addOnFailureListener {
                updateUI(null)
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {

            val i = Intent(this, Menu::class.java)
            startActivity(i)
        } else {
            Log.d(TAG, "Login falhou")
            Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
        }
    }
}
