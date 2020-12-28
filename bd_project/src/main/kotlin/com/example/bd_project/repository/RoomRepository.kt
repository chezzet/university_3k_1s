package com.example.bd_project.repository

import com.example.bd_project.model.Room
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoomRepository : JpaRepository<Room, UUID> {
    fun findAllByDeletedIsFalse() : List<Room>
}