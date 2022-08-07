package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class LabTestOrderAttachment(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("file")
    var file: FileDescriptor? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("labTestOrder")
    val labTestOrder: LabTestOrder? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
