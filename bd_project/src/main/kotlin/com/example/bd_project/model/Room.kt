package com.example.bd_project.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "room", schema = "public")
data class Room(
    @Id val id: UUID,
    val name: String,
    val description: String,
    val capacity: Int,
    val technicalTimestamp: Date?,
    val deleted: Boolean = false
)
