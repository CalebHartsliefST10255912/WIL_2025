package com.example.wil_byte_horizon.ui.event

import android.text.format.DateFormat
import com.google.firebase.Timestamp

object DateFormatter {
    fun formatRange(start: Timestamp?, end: Timestamp?): String {
        if (start == null && end == null) return ""
        if (start != null && end == null) return format(start)
        if (start == null && end != null) return format(end)
        // both present
        return "${format(start!!)} – ${format(end!!)}"
    }

    private fun format(ts: Timestamp): String {
        val date = ts.toDate()
        // Example: Tue, 29 Oct 2025 • 14:00
        return DateFormat.format("EEE, dd MMM yyyy • HH:mm", date).toString()
    }
}
