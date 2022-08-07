package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ServiceFee(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("serviceDto")
    val service: Service? = null,

    @SerializedName("fee")
    val fee: Double? = null,

    @SerializedName("fromDate")
    val fromDate: Date? = null,

    @SerializedName("toDate")
    val toDate: Date? = null,

    @SerializedName("isUsing")
    val inUse: Boolean? = null
)
