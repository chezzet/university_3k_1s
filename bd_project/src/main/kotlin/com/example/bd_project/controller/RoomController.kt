package com.example.bd_project.controller

import com.example.bd_project.model.Room
import com.example.bd_project.repository.RoomRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/room")
class RoomController (
    private val roomRepository: RoomRepository
) {

    @GetMapping
    fun getRooms() : ResponseEntity<List<Room>> {
        return ResponseEntity.ok(roomRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getRoom(@PathVariable("id") id: UUID) : ResponseEntity<Room> {
        return ResponseEntity.of(roomRepository.findById(id))
    }

    @PostMapping
    fun save(@RequestBody room: Room) : ResponseEntity<Room> {
        return ResponseEntity.ok(roomRepository.save(room))
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable("id") id: UUID) {
        roomRepository.findById(id)
            .ifPresent {
                roomRepository.save(Room(
                    it.id,
                    it.name,
                    it.description,
                    it.capacity,
                    it.technicalTimestamp,
                    true
                ))
            }
    }
}