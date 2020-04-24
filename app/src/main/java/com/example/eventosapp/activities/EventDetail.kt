package com.example.eventosapp.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.stream.UrlLoader
import com.example.eventosapp.Event
import com.example.eventosapp.R
import kotlinx.android.synthetic.main.activity_event_detail.*

class EventDetail : AppCompatActivity() {

    var eventImage = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        getIncommingIntent()

        event_image_detail.setOnClickListener {
            Glide.with(this).load(eventImage).into(full_screen_image_event_detail)
            full_screen_image_event_detail.visibility = View.VISIBLE
            blurry_layout_event_detail.visibility = View.VISIBLE
        }

        full_screen_image_event_detail.setOnClickListener {
            it.visibility = View.GONE
            blurry_layout_event_detail.visibility = View.GONE

        }
    }

    private fun getIncommingIntent() {
        if (this.intent.hasExtra("event")) {
            val event: Event = this.intent.getSerializableExtra("event") as Event

            setEventDetail(event)
        }
    }

    private fun setEventDetail(event: Event) {
        Glide.with(this).load(event.eventImageUrl).into(event_image_detail)

        eventImage = Uri.parse(event.eventImageUrl)
        event_creator_detail_text_view.text = event.creatorUsername
        event_name_detail_text_view.text = event.name
        event_location_detail_text_view.text = event.location
        event_time_detail_text_view.text = event.time
        event_date_detail_text_view.text = event.date
        event_description_detail_text_view.text = event.description
    }
}
