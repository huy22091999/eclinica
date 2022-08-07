package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("pageIndex")
    val pageIndex:Int ?= 0,

    @SerializedName("pageSize")
    val pageSize:Int ?= 10
    )
