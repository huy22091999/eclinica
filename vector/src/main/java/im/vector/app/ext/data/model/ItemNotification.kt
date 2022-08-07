package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class ItemNotification(

    @SerializedName("items")
    var items: List<DataItemNotification> ?= null,
    @SerializedName("total")
    var total: Int ?= null
)
