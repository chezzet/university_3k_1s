package com.example.class_booker.ui.rooms

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.alamkanak.weekview.WeekView
import com.example.class_booker.R
import com.example.class_booker.data.model.Room
import com.example.class_booker.ui.weekViewUtils.WeekViewEventAdapter
import com.example.class_booker.utils.convertToLocalFromString
import com.example.class_booker.utils.projectFormatted
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RoomsFragment : Fragment() {

    private val roomsViewModel: RoomsViewModel by viewModels {
        RoomsViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rooms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val roomRecyclerAdapter = RoomRecyclerAdapter(this::bookDialog)
        recyclerView.adapter = roomRecyclerAdapter

        roomsViewModel.allRooms.observe(viewLifecycleOwner) {
            roomRecyclerAdapter.submitList(it)
        }

        roomsViewModel.bookingMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun bookDialog(room: Room) {
        val popUp = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.room_popup, null)
        popUp.setContentView(view)

        var fromDate = LocalDate.now()
        var fromTime = LocalTime.now()
        var toDate = LocalDate.now()
        var toTime = LocalTime.now().plusHours(1)

        view.findViewById<EditText>(R.id.from_date).text = SpannableStringBuilder(fromDate.toString())
        view.findViewById<EditText>(R.id.from_date).setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus) {
                pickDate(fromDate, it) { updated ->
                    (it as EditText).text = SpannableStringBuilder(updated.toString())
                    fromDate = updated
                }
            }
        }
        view.findViewById<EditText>(R.id.from_time).text = SpannableStringBuilder(fromTime.projectFormatted())
        view.findViewById<EditText>(R.id.from_time).setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus) {
                pickTime(fromTime, it) { updated ->
                    (it as EditText).text = SpannableStringBuilder(updated.projectFormatted())
                    fromTime = updated
                }
            }
        }
        view.findViewById<EditText>(R.id.to_date).text = SpannableStringBuilder(toDate.toString())
        view.findViewById<EditText>(R.id.to_date).setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus) {
                pickDate(toDate, it) { updated ->
                    (it as EditText).text = SpannableStringBuilder(updated.toString())
                    toDate = updated
                }
            }
        }
        view.findViewById<EditText>(R.id.to_time).text = SpannableStringBuilder(toTime.projectFormatted())
        view.findViewById<EditText>(R.id.to_time).setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus) {
                pickTime(toTime, it) { updated ->
                    (it as EditText).text = SpannableStringBuilder(updated.projectFormatted())
                    toTime = updated
                }
            }
        }

        val weekView = view.findViewById<WeekView>(R.id.week_view)
        val adapter = WeekViewEventAdapter() { from, to ->
            roomsViewModel.getBookings(from, to)
        }
        roomsViewModel.selectedRoom.value = room.id
        roomsViewModel.getBookings(weekView.firstVisibleDate.time, weekView.lastVisibleDate.time)
        roomsViewModel.bookings.observe(this) {
            adapter.submitList(it)
        }
        weekView.adapter = adapter

        view.findViewById<Button>(R.id.bookButton).setOnClickListener {
            val fromDateTime = LocalDateTime.of(fromDate, fromTime).atZone(ZoneId.of("UTC"))
            val toDateTime = LocalDateTime.of(toDate, toTime).atZone(ZoneId.of("UTC"))

            if (fromDateTime.isAfter(toDateTime)) {
                Toast.makeText(requireContext(), "Start Date Time should be less then End Date Time", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            roomsViewModel.book(
                convertToLocalFromString(
                    fromDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"))
                ),
                convertToLocalFromString(
                    toDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"))
                ),
                view.findViewById<EditText>(R.id.title).text.toString()
            )
            popUp.dismiss()
        }

        popUp.show()
    }

    private fun pickDate(current: LocalDate, view: View, callback: (updated: LocalDate) -> Unit) {
        val datePickerDialog = DatePickerDialog(
            view.context,
            { _, year, month, dayOfMonth -> callback(LocalDate.of(year, month + 1, dayOfMonth)) },
            current.year, current.monthValue - 1, current.dayOfMonth
        )
        datePickerDialog.setOnCancelListener { callback(current) }
        datePickerDialog.show()
    }

    private fun pickTime(current: LocalTime, view: View, callback: (updated: LocalTime) -> Unit) {
        val timePickerDialog = TimePickerDialog(
            view.context,
            { _, hourOfDay, minute -> callback(LocalTime.of(hourOfDay, minute)) },
            current.hour, current.minute, true
        )
        timePickerDialog.setOnCancelListener { callback(current) }
        timePickerDialog.show()
    }
}