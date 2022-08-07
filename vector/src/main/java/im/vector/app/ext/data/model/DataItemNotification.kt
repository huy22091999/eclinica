package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class DataItemNotification(

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("createdBy")
    val createdBy: String? = null,

    @SerializedName("innitiatedDate")
    val innitiatedDate: Long? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("userName")
    val userName: String? = null
)
