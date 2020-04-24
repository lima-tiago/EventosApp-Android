package com.example.eventosapp.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.eventosapp.R
import com.example.eventosapp.User
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetail : AppCompatActivity() {

    var userImage = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        getIncommingIntent()

        user_image_detail.setOnClickListener {
            Glide.with(this).load(userImage).into(full_screen_image_user_detail)
            full_screen_image_user_detail.visibility = View.VISIBLE
            blurry_layout_user_detail.visibility = View.VISIBLE
        }

        full_screen_image_user_detail.setOnClickListener {
            it.visibility = View.GONE
            blurry_layout_user_detail.visibility = View.GONE
        }
    }

    private fun getIncommingIntent() {
        if (this.intent.hasExtra("user")){
            val user:User = this.intent.getSerializableExtra("user") as User

            setUserDetail(user)
        }
    }

    private fun setUserDetail(user: User) {
        Glide.with(this).load(user.profileImageUrl).into(user_image_detail)

        userImage = Uri.parse(user.profileImageUrl)
        username_user_detail_text_view.text = user.username
        created_events_user_detail_text_view.text = "Eventos criados: " + user.numberOfCreatedEvents
        email_user_detail_text_view.text = user.email
    }
}
