package im.vector.app.ext.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.resources.MaterialResources
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import im.vector.app.ext.utils.dpToPx


fun EditText.parseDate(): Date? {
    if (text.trim().isEmpty()) {
        return null
    }

    val s = text.trim().toString()
    val arr = s.split("/")
    if (arr.size == 3) {
        var ds = ""

        ds += when (arr[0].length) {
            0 -> ""
            1 -> "0" + arr[0] + "/"
            2 -> arr[0] + "/"
            else -> ""
        }

        ds += when (arr[1].length) {
            0 -> ""
            1 -> "0" + arr[1] + "/"
            2 -> if (arr[1].toInt() <= 12) (arr[1] + "/") else ""
            else -> ""
        }

        ds += if (arr[2].length == 4) arr[2] else ""

        if (ds.length != 10) return null

        // check month end
        val midMonth = LocalDate.of(ds.substring(6).toInt(), ds.substring(3, 5).toInt(), 15)
        val endOfMonth = midMonth.lengthOfMonth()
        if (ds.substring(0, 2).toInt() > endOfMonth) {
            ds = endOfMonth.toString() + ds.substring(2)
        }

        val date = LocalDate.parse(ds, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val instant: Instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    return null
}

fun Date.format(format: String? = null): String {
    val ld = toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return ld.format(DateTimeFormatter.ofPattern(format ?: "dd/MM/yyyy"))
}

fun EditText.style(layout: TextInputLayout, activity: Activity) {
    var theme = activity.theme

      if (this.text.isNullOrEmpty()) {
        layout.boxStrokeWidth = 0
        layout.boxBackgroundColor = resources.getColor(R.color.background_edit_text, theme)
    } else {
        layout.boxStrokeWidth = dpToPx(1, activity)
        layout.boxBackgroundColor = resources.getColor(R.color.white, theme)
    }

    this.setOnFocusChangeListener { _, b ->
        if (b) {
            layout.boxBackgroundColor = resources.getColor(R.color.white, theme)
        } else {
            if (this.text.isNullOrEmpty()) {
                layout.boxStrokeWidth = 0
                layout.boxBackgroundColor = resources.getColor(R.color.background_edit_text, theme)
            } else {
                layout.boxStrokeWidth = dpToPx(1, activity)
                layout.boxBackgroundColor = resources.getColor(R.color.white, theme)
            }
        }
    }
}

fun Fragment.formatdate(date: TextInputEditText, dateTil: TextInputLayout) {
    date.addTextChangedListener(object : TextWatcher {
        private var current = ""
        private val ddmmyyyy = "DDMMYYYY"
        private val cal: Calendar = Calendar.getInstance()
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            dateTil.error = ""
            try {
                if (s.toString() != current) {
                    var clean = s.toString().replace("[^\\d.]".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]".toRegex(), "")
                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean == cleanC) sel--
                    if (clean.length < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length)
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        var day = clean.substring(0, 2).toInt()
                        var mon = clean.substring(2, 4).toInt()
                        var year = clean.substring(4, 8).toInt()
                        if (mon > 12) mon = 12
                        cal.set(Calendar.MONTH, mon - 1)
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal.set(Calendar.YEAR, year)
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012
                        day =
                            if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(
                                Calendar.DATE
                            ) else day
                        clean = String.format("%02d%02d%02d", day, mon, year)
                    }
                    clean = String.format(
                        "%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8)
                    )
                    sel = if (sel < 0) 0 else sel
                    current = clean
                    date.setText(current)
                    date.setSelection(if (sel < current.length) sel else current.length)
                }
            } catch (e: Exception) {
                dateTil.error = getString(R.string.date_invalid)
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun afterTextChanged(s: Editable) {
        }
    })

}

fun String?.isNullOrEmptyAlt(): Boolean {
    if (this == null) {
        return true
    }

    return this.isBlank()
}

fun TextInputEditText.toStringAlt(): String {
    val s = text.toString()
    if (s.isBlank()) {
        return ""
    }

    return s.replace("\\s".toRegex(), " ").trim()
}

fun String?.onlyNumber(): Boolean {
    if (this != null) {
        for (item in this) {
            if (!Character.isDigit(item))
                return false
        }
    } else return false
    return true
}
