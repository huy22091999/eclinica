package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class FiledtoItems(
    @SerializedName("content") var content: ArrayList<FiledtoContent> = arrayListOf(),
    @SerializedName("empty") var empty : Boolean? = false
    )
