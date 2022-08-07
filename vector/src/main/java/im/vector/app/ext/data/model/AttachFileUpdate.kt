package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class AttachFileUpdate (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("description")
    var description: String? = null,
)
