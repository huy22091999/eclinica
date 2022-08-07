package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class AppointmentFilter(
    @SerializedName("pageIndex")
    val pageIndex: Int? = 1,

    @SerializedName("pageSize")
    val pageSize: Int? = 10,

    @SerializedName("text")
    val keyword: String? = null,

    @SerializedName("patientId")
    val patientId: String? = null,

    @SerializedName("slotId")
    val slotId: String? = null,

    @SerializedName("status")
    val status: Int? = null
)
