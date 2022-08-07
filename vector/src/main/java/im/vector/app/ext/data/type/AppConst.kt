package im.vector.app.ext.data.type

import android.annotation.SuppressLint
import android.content.Context
import im.vector.app.R
import java.text.SimpleDateFormat
import java.util.*

class AppConst {
    companion object {

        fun gender(value: String, context: Context): String {
            return when (value) {
                "M" -> context.getString(R.string.gender_male)
                "F" -> context.getString(R.string.gender_female)
                else -> ""
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getAgeFromDate(date: Date?): Int {
            if (date == null) return 0
            val dob: Calendar = Calendar.getInstance()
            val today: Calendar = Calendar.getInstance()
            dob.time = date
            val year: Int = dob.get(Calendar.YEAR)
            val month: Int = dob.get(Calendar.MONTH)
            val day: Int = dob.get(Calendar.DAY_OF_MONTH)
            dob.set(year, month + 1, day)
            var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            return age
        }

        fun motorFunction(value: Int, context: Context): String {
            return when (value) {
                1 -> context.getString(R.string.motor_function_normal)
                2 -> context.getString(R.string.motor_function_lying_in_bed)
                3 -> context.getString(R.string.motor_function_walkable)
                else -> ""
            }
        }

        fun degreeOfAdherenceTreatment(value: Int, context: Context): String {
            return when (value) {
                1 -> context.getString(R.string.good)
                2 -> context.getString(R.string.medium)
                3 -> context.getString(R.string.least)
                4 -> context.getString(R.string.unknown)
                else -> ""
            }
        }

        fun receiveType(value: Int, context: Context): String {
            return when (value) {
                1 -> context.getString(R.string.at_home)
                3 -> context.getString(R.string.at_drug_store)
                else -> ""
            }
        }

        fun labTestOrderStatus(value: Int, context: Context): String {
            return when (value) {
                1 -> context.getString(R.string.order_from_clinic)
                2 -> context.getString(R.string.patient_choose_service)
                3 -> context.getString(R.string.have_resulted)
                else -> ""
            }
        }

        fun encounterStatus(value: Int?, context: Context): String {
            return when (value) {
                1 -> context.getString(R.string.draft)
                2 -> context.getString(R.string.waiting_confirm_labtest)
                3 -> context.getString(R.string.waiting_labtest_confirm)
                4 -> context.getString(R.string.waiting_appointment)
                5 -> context.getString(R.string.waiting_encounter)
                6 -> context.getString(R.string.encountered)
                else -> ""
            }
        }

        fun prescriptionStatusDrug(value: Int?, context: Context): String {
            return when (value) {
                1 -> context.getString(R.string.just_received)
                2 -> context.getString(R.string.processing)
                3 -> context.getString(R.string.sent)
                4 -> context.getString(R.string.received)
                else -> ""
            }
        }

        const val PRESCRIPTION_STATUS_NEW = 1
        const val PRESCRIPTION_STATUS_SEND = 3
        const val PRESCRIPTION_STATUS_RECEIVED = 4

    }
}
