package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class PatientFile (
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("contentType")
    val contentType: String? = null,
    @SerializedName("contentSize")
    val contentSize: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("filePath")
    val filePath: String? = null
)
