package com.example.bd_project.controller

import com.example.bd_project.model.Booking
import com.example.bd_project.repository.BookingRepository
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/booking")
class BookingController(
    private val bookingRepository: BookingRepository
) {

    @GetMapping("/user")
    fun getAllBetweenByUser(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") from: Date,
                            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") to: Date,
                            @RequestParam("userId") userId: UUID) : List<Booking> {
        return bookingRepository.findAllByFromTimeBetweenOrAndToTimeBetweenByUser(from, to, userId).map { it.toUploaded() }
    }

    @GetMapping("/room")
    fun getAllBetweenByRoom(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") from: Date,
                            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") to: Date,
                            @RequestParam("roomId") roomId: UUID) : List<Booking> {
        return bookingRepository.findAllByFromTimeBetweenOrAndToTimeBetweenByRoom(from, to, roomId).map { it.toUploaded() }
    }

    @PostMapping("/all")
    fun saveAll(@RequestBody bookings: List<Booking>): List<Booking> {
        val oldBookings = bookingRepository.findAllById(bookings.map { it.id })
        val oldBookingsMap = oldBookings.associateBy { it.id }
        val newBookings = bookings.filter { oldBookingsMap[it.id]?.technicalTimestamp?.before(it.technicalTimestamp) ?: true }
        return bookingRepository.saveAll(newBookings).map {it.toUploaded()}
    }

    @GetMapping("/user/{id}")
    fun getAllByUser(@PathVariable("id") userId: UUID) : List<Booking> {
        return bookingRepository.findAllByUserId(userId).map { it.toUploaded() }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: UUID): ResponseEntity<Booking> {
        return ResponseEntity.of(bookingRepository.findById(id).map { it.toUploaded() })
    }

    @PostMapping
    fun save(@RequestBody booking: Booking) : Booking {
        val old = bookingRepository.findById(booking.id)
        return if (old.isEmpty || old.get().technicalTimestamp.before(booking.technicalTimestamp)) {
            bookingRepository.save(booking)
        } else {
            old.get()
        }.toUploaded()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: UUID) {
        bookingRepository.deleteById(id)
    }
}