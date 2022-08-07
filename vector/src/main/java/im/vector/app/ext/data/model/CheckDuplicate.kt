package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class CheckDuplicate(
    @SerializedName("description")
    var description: String? = null,

    @SerializedName("isIdNumBer")
    var isIdNumBer: Boolean? = null,

    @SerializedName("isEmail")
    var isEmail: Boolean? = null,

    @SerializedName("isPhoneNumber")
    var isPhoneNumber: Boolean? = null

)
