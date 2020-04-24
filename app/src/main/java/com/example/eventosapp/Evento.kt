package com.example.eventosapp

import java.io.Serializable


data class User(
    val uid: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val numberOfCreatedEvents: String = ""
) : Serializable

class Event(
    val uid: String = "",
    val name: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val description: String = "",
    val eventImageUrl: String = "",
    val creatorUid: String = "",
    val creatorUsername: String = ""
) : Serializable