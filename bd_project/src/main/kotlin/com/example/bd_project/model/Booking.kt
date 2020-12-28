package com.example.bd_project.model

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "booking", schema = "public")
class Booking(
    @Id val id: UUID,
    @Column(name = "room_id") val roomId: UUID,
    @Column(name = "user_id") val userId: UUID,
    val title: String,
    val fromTime: Date,
    val toTime: Date,
    val technicalTimestamp: Date,
    val deleted: Boolean = false,
    val uploaded: Boolean = true,

    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = false, insertable = false, updatable = false)
    @ManyToOne(targetEntity = User::class, cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY, optional = false)
    val user: User? = null,

    @JoinColumn(name = "room_id", referencedColumnName = "id", unique = false, insertable = false, updatable = false)
    @ManyToOne(targetEntity = Room::class, cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY, optional = false)
    val room: Room? = null
) {
    fun toUploaded() = Booking(
        id,
        roomId,
        userId,
        title,
        fromTime,
        toTime,
        technicalTimestamp,
        deleted,
        true,
        user,
        room
    )
}