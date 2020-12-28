package com.example.class_booker.data

import java.util.*

object UserHolder {
    var id: UUID? = UUID.randomUUID()
    var name: String? = ""
    var role: Int = -1

    fun isEmpty() = id?.let { true } ?: false
}