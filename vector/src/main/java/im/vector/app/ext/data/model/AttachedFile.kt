package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class AttachedFile(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("type")
    var type: Int? = null,

    @SerializedName("attackImageType")
    var attackImageType: Int? = null,

    @SerializedName("patient")
    var patient: Patient? = null,

    @SerializedName("file")
    var file: FileDescriptor? = null,

    @SerializedName("description")
    var description: String? = null

)
