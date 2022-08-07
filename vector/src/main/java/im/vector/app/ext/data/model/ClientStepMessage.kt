package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class ClientStepMessage(

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("step")
    val step: Int? = null

)
