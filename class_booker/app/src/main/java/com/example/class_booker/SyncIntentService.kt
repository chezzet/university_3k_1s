package com.example.class_booker

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.datasource.local.dao.BookingDao
import com.example.class_booker.data.datasource.local.dao.RoomDao
import com.example.class_booker.data.datasource.local.dao.UserDao
import com.example.class_booker.data.datasource.local.repo_iml.LocalBookingDataSource
import com.example.class_booker.data.datasource.local.repo_iml.LocalRoomDataSource
import com.example.class_booker.data.datasource.local.repo_iml.LocalUserDataSource
import com.example.class_booker.data.repository.BookingRepository
import com.example.class_booker.data.repository.RoomRepository
import com.example.class_booker.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class SyncIntentService : IntentService("SyncIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val userRepository = UserRepository(LocalUserDataSource(UserDao(ServiceLocator.buildDBHelper(applicationContext))))
        val roomRepository = RoomRepository(LocalRoomDataSource(RoomDao(ServiceLocator.buildDBHelper(applicationContext))))
        val bookingRepository = BookingRepository(LocalBookingDataSource(BookingDao(ServiceLocator.buildDBHelper(applicationContext))))

        CoroutineScope(EmptyCoroutineContext).launch {
            userRepository.sync()
            roomRepository.sync()
            bookingRepository.sync()
        }
    }

    companion object {

        @JvmStatic
        fun startSync(context: Context) {
            val intent = Intent(context, SyncIntentService::class.java)
            context.startService(intent)
        }

    }
}