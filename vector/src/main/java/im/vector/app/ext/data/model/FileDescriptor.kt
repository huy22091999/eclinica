package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class FileDescriptor(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("contentType")
    var contentType: String? = null,

    @SerializedName("contentSize")
    var contentSize: Long? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("extension")
    var extension: String? = null,

    @SerializedName("filePath")
    var filePath: String? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
