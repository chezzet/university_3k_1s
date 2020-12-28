package com.example.class_booker.ui.overview

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alamkanak.weekview.WeekView
import com.example.class_booker.R
import com.example.class_booker.ui.weekViewUtils.WeekViewEventAdapter
import com.example.class_booker.utils.toFormattedString

class OverviewFragment : Fragment() {

    private val overviewViewModel: OverviewViewModel by viewModels {
        OverviewViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weekView = view.findViewById<WeekView>(R.id.week_view)
        overviewViewModel.fromDate.value = weekView.firstVisibleDate.time
        overviewViewModel.toDate.value = weekView.lastVisibleDate.time
        overviewViewModel.getBookings()
        val weekViewEventAdapter = WeekViewEventAdapter({
            val popUp = Dialog(requireContext())
            val popUpView = layoutInflater.inflate(R.layout.overview_popup, null, false)
            popUp.setContentView(popUpView)

            popUpView.findViewById<TextView>(R.id.title).text = it.title
            popUpView.findViewById<TextView>(R.id.room).text = it.roomName
            popUpView.findViewById<TextView>(R.id.from_datetime).text = it.fromTime.toFormattedString()
            popUpView.findViewById<TextView>(R.id.to_datetime).text = it.toTime.toFormattedString()
            popUpView.findViewById<Button>(R.id.deleteButton).setOnClickListener { _ ->
                overviewViewModel.delete(it).invokeOnCompletion {
                    popUp.dismiss()
                    weekView.invalidate()
                }
            }

            popUp.show()
        }) { from, to ->
            overviewViewModel.fromDate.value = from
            overviewViewModel.toDate.value = to
            overviewViewModel.getBookings()
        }
        weekView.adapter = weekViewEventAdapter
        overviewViewModel.bookings.observe(viewLifecycleOwner) {
            weekViewEventAdapter.submitList(it)
        }
    }
}