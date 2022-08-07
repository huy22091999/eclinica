package im.vector.app.ext.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PatientMedicalRecordFile(
    @SerializedName("file") val file: PatientFile? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("type") val type: Int? = null,
    @SerializedName("attackImageType") val attackImageType: Int? = null
)

