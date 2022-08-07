package im.vector.app.ext.data.type

import android.content.Context
import im.vector.app.R

enum class ServiceType {

    HEALTH_INSURANCE,

    HAS_FEE,

    FREE,

    OTHER;

    fun toLocalizedString(context: Context): String {
        return when {
            this == HEALTH_INSURANCE -> context.getString(R.string.service_type_shi)

            this == HAS_FEE -> context.getString(R.string.service_type_paid)

            this == FREE -> context.getString(R.string.service_type_free)

            this == OTHER -> context.getString(R.string.service_type_other)

            else -> "-"
        }
    }
}
