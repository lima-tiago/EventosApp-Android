package com.example.eventosapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventosapp.R
import com.example.eventosapp.activities.UserDetail

class AdapterUsers(val nome: String) : RecyclerView.Adapter<AdapterUsers.UserViewHolder>() {
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindUser(nome: String) {


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_user, parent, false)
        return UserViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(nome)

        holder.itemView.setOnClickListener {
            val i = Intent(holder.itemView.context, UserDetail::class.java)
            holder.itemView.context.startActivity(i)

        }
    }
}