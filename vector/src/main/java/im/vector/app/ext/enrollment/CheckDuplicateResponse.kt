package im.vector.app.ext.enrollment

import com.google.gson.annotations.SerializedName

data class CheckDuplicateResponse(

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("data")
    val data: Boolean? = null

)
