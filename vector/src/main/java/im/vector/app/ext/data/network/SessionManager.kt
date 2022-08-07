package im.vector.app.ext.data.network

import android.content.Context
import android.content.SharedPreferences
import im.vector.app.R
import timber.log.Timber

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager(context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val CLINIC="clinic"
        const val CLINICID="clinicid"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun saveClinic(clinic: String) {
        val editor = prefs.edit()
        editor.putString(CLINIC, clinic)
        editor.apply()
    }
    fun saveClinicId(clinic: String) {
        val editor = prefs.edit()
        editor.putString(CLINICID, clinic)
        editor.apply()
    }
    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    fun fetchClinic(): String? {
        return prefs.getString(CLINIC, null)
    }

    fun fetchClinicId(): String? {
        return prefs.getString(CLINICID, null)
    }
}
