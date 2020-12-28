package com.example.class_booker.ui.weekViewUtils

import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import com.example.class_booker.data.model.Booking
import java.util.*

class WeekViewEventAdapter(
    private val clickCallback: ((Booking) -> Unit)? = null,
    private val loadingCallback: (from: Date, to: Date) -> Unit
) : WeekView.SimpleAdapter<Booking>() {

    override fun onRangeChanged(firstVisibleDate: Calendar, lastVisibleDate: Calendar) {
        super.onRangeChanged(firstVisibleDate, lastVisibleDate)
        loadingCallback(firstVisibleDate.time, lastVisibleDate.time)
    }

    override fun onEventClick(data: Booking) {
        super.onEventClick(data)
        clickCallback?.let { it.invoke(data) }
    }

    override fun onCreateEntity(item: Booking): WeekViewEntity {
        return WeekViewEntity.Event.Builder(item)
            .setId(item.id.hashCode().toLong())
            .setTitle(item.title)
            .setSubtitle("User: " + item.userName + "\nRoom: " + item.roomName)
            .setStartTime(Calendar.getInstance().apply { time = item.fromTime })
            .setEndTime(Calendar.getInstance().apply { time = item.toTime })
            .build()
    }
}