package im.vector.app.ext.data.type

import android.content.Context
import im.vector.app.R

enum class Gender {
    F, // Nam
    M; // Nữ

    fun toLocalizedString(context: Context): String {
        return if (this == M) context.getString(R.string.gender_male)
        else context.getString(R.string.gender_female)
    }
}
