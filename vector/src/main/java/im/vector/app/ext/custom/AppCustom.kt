package im.vector.app.ext.custom

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.textfield.TextInputEditText
import im.vector.app.core.extensions.hideKeyboard
import java.text.SimpleDateFormat
import java.util.*

class AppCustom {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun TextInputEditText.transformIntoDatePicker(
            context: Context,
            currentDate: Date? = null,
            maxDate: Date? = null,
            minDate: Date? = null
        ) {
//            isFocusableInTouchMode = false
            isClickable = true
           // isFocusable = false


            val myCalendar = Calendar.getInstance()
            currentDate?.also { myCalendar.time = it }
            val datePickerOnDataSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    setText(sdf.format(myCalendar.time))
                }
//
//            setOnClickListener {
//                DatePickerDialog(
//                    context, datePickerOnDataSetListener, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH)
//                ).run {
//                    maxDate?.time?.also { datePicker.maxDate = it }
//                    minDate?.time?.also { datePicker.minDate = it }
//                    show()
//                }
//            }

            setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                hideKeyboard()
                val drawableRight  = 2
                if (p1!!.action == MotionEvent.ACTION_UP){
                    if (p1.rawX >= right -compoundDrawables[drawableRight].bounds.width())
                    {
                        DatePickerDialog(
                            context, datePickerOnDataSetListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                        ).run {
                            maxDate?.time?.also { datePicker.maxDate = it }
                            minDate?.time?.also { datePicker.minDate = it }
                            show()
                        }
                        return true
                    }
                }
                return  false
            }
        })
        }
    }
}
