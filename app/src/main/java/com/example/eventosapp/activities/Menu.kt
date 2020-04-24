package com.example.eventosapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventosapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_menu.*


class Menu : AppCompatActivity() {
    private val TAG = "Menu"
    private val auth = FirebaseAuth.getInstance()
    private val userUid = auth.currentUser!!.uid
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val userRef = rootRef.child("users").child(userUid)
    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        getUserInfo()

        event_list_button.setOnClickListener {
            val i = Intent(this, EventRecycler::class.java)
            startActivity(i)
        }

        user_list_button.setOnClickListener {
            val i = Intent(this, UserRecycler::class.java)
            startActivity(i)
        }

        sign_out_button.setOnClickListener {
            performSignOut()
        }
    }

    private fun performSignOut() {
        auth.signOut()
        finish()
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Para sair da aplicação clique em 'sair'.", Toast.LENGTH_SHORT)
            .show()
    }


    private fun getUserInfo() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "cancelado")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, "nao cancelado")
                username = p0.child("username").value.toString()
                Toast.makeText(this@Menu, "Bem vindo, ${username}", Toast.LENGTH_SHORT).show()

            }
        })
        runOnUiThread {
        }
    }

//    private fun getUserData() {
//        Log.d(TAG, "hello")
//
//        val uid = auth.currentUser!!.uid
//        val rootRef = FirebaseDatabase.getInstance().reference
//        val userRef = rootRef.child("users").child(uid)
//
//        userRef.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d(TAG, "cancelado")
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                Log.d(TAG, "nao cancelado")
//                Log.d(TAG, "${p0.exists()}")
//
//                if (p0.exists()) {
//                    val user = p0.getValue(User::class.java)
//                    if (user != null) {
//                        Log.d(TAG, user.uid)
//                        Log.d(TAG, user.username)
//                        Log.d(TAG, user.password)
//                        Log.d(TAG, user.email)
//                        Log.d(TAG, user.profileImageUrl)
//
//                    }
//                }
//            }
//
//        })
//    }
}
