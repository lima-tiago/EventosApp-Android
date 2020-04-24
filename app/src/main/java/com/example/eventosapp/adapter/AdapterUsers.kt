package com.example.eventosapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventosapp.R
import com.example.eventosapp.User
import com.example.eventosapp.activities.UserDetail

class AdapterUsers(val user_list: ArrayList<User>, val context: Context) :
    RecyclerView.Adapter<AdapterUsers.UserViewHolder>() {
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindUser(user: User) {

            itemView.findViewById<TextView>(R.id.user_username_text_view_cell).text = user.username
            itemView.findViewById<TextView>(R.id.user_num_events_created_text_view_cell).text = user.numberOfCreatedEvents

            Glide.with(itemView).load(user.profileImageUrl).into(itemView.findViewById(R.id.user_image_cell))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_user, parent, false)
        return UserViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return user_list.count()
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(user_list[position])

        holder.itemView.setOnClickListener {
            val i = Intent(holder.itemView.context, UserDetail::class.java)
            i.putExtra("user", user_list[position])

            holder.itemView.context.startActivity(i)

        }
    }
}