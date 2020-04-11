package com.example.eventosapp


class User(
    val uid: String,
    val username: String,
    val profileImageUrl: String
)

class Event(
    val uid: String,
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val eventImageUrl: String,
    val creator: String
)