package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class EncounterSlot(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("isBooked")
    val booked: Boolean? = null,

    @SerializedName("slotIndex")
    val index: Int? = null,

    @SerializedName("start")
    val start: Date? = null,

    @SerializedName("end")
    val end: Date? = null,

    @SerializedName("hasEdit")
    val hasEdit: Boolean? = null,

    @SerializedName("quantity")
    val quantity: Int? = null,

    @SerializedName("register")
    val registered: Int? = null

) {
    override fun toString(): String {
        return name ?: "-"
    }
}
data class EncounterSlotFilter(
    @SerializedName("currentDate")
    var currentDate: Date? = Date(),
)

