package com.example.class_booker.ui.rooms

import android.content.Context
import androidx.lifecycle.*
import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.UserHolder
import com.example.class_booker.data.datasource.local.dao.BookingDao
import com.example.class_booker.data.datasource.local.dao.RoomDao
import com.example.class_booker.data.datasource.local.repo_iml.LocalBookingDataSource
import com.example.class_booker.data.datasource.local.repo_iml.LocalRoomDataSource
import com.example.class_booker.data.model.Booking
import com.example.class_booker.data.model.Room
import com.example.class_booker.data.repository.BookingRepository
import com.example.class_booker.data.repository.RoomRepository
import kotlinx.coroutines.launch
import java.util.*

class RoomsViewModel(
    private val roomRepository: RoomRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val allRooms: LiveData<List<Room>> = roomRepository.findAll().asLiveData()

    val selectedRoom = MutableLiveData<UUID>()

    val bookings = MutableLiveData<List<Booking>>(emptyList())

    val bookingMessage = MutableLiveData<String>()

    fun getBookings(from: Date, to: Date) = viewModelScope.launch {
        bookingRepository.findAllBetween(from, to, selectedRoom.value!!)
            .asLiveData().observeForever { bookings.postValue(it) }
    }

    fun book(from: Date, to: Date, title: String) = viewModelScope.launch {
        val available = bookingRepository.checkAvailability(from, to, selectedRoom.value!!)
        if (!available) {
            bookingMessage.postValue("Already booked")
            return@launch
        }
        bookingRepository.save(Booking(UUID.randomUUID(), title, selectedRoom.value!!, UserHolder.id!!, from, to))
        bookingMessage.postValue("Booked!")
    }
}

class RoomsViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomsViewModel::class.java)) {
            return RoomsViewModel(
                RoomRepository(
                    LocalRoomDataSource(
                        RoomDao(ServiceLocator.buildDBHelper(context))
                    )
                ),
                BookingRepository(
                    LocalBookingDataSource(
                        BookingDao(ServiceLocator.buildDBHelper(context))
                    )
                ),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}