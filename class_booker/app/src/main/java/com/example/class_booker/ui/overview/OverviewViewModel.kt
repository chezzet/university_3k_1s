package com.example.class_booker.ui.overview

import android.content.Context
import androidx.lifecycle.*
import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.datasource.local.dao.BookingDao
import com.example.class_booker.data.datasource.local.repo_iml.LocalBookingDataSource
import com.example.class_booker.data.model.Booking
import com.example.class_booker.data.repository.BookingRepository
import kotlinx.coroutines.launch
import java.util.*

class OverviewViewModel(private val bookingRepository: BookingRepository) : ViewModel() {

    val fromDate: MutableLiveData<Date> = MutableLiveData()

    val toDate: MutableLiveData<Date> = MutableLiveData()

    val bookings = MutableLiveData<List<Booking>>()

    fun getBookings() = viewModelScope.launch {
        bookingRepository.findAllBetween(fromDate.value!!, toDate.value!!)
            .asLiveData().observeForever {
                bookings.postValue(it)
            }
    }

    fun delete(booking: Booking) = viewModelScope.launch {
        bookingRepository.update(
            Booking(
                booking.id,
                booking.title,
                booking.roomId,
                booking.userId,
                booking.fromTime,
                booking.toTime,
                deleted = true
            )
        )
    }
}

class OverviewViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(BookingRepository(LocalBookingDataSource(BookingDao(ServiceLocator.buildDBHelper(context))))) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}