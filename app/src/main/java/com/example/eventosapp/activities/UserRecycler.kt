package com.example.eventosapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventosapp.Event
import com.example.eventosapp.R
import com.example.eventosapp.User
import com.example.eventosapp.adapter.AdapterUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_recycler.*

class UserRecycler : AppCompatActivity() {
    private val TAG = "UserRecycler"
    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser!!.uid
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val userRef = rootRef.child("users")

    var user_list: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_recycler)

        getUsersFromFirebase()
    }

    private fun getUsersFromFirebase() {
        progress_bar_user_recycler.visibility = View.VISIBLE
        Log.d(TAG, "pegando usuarios")
        user_list.clear()

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "cancelado")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, "nao cancelado")
                Log.d(TAG, "${p0.exists()}")
                Log.d(TAG, "${p0.children.count()}")

                if (p0.exists()) {
                    for (p in p0.children){

                        val user = p.getValue(User::class.java)
                        if (user != null) {
                            user_list.add(user)


                        }
                        Log.d(TAG, "evento adicionado ${user?.username}")
                    }
                }
                runOnUiThread {
                    configureAdapter()
                }
            }

        })
    }

    private fun configureAdapter() {
        progress_bar_user_recycler.visibility = View.GONE
        recycler_users.adapter = AdapterUsers(user_list,this)
        recycler_users.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}
