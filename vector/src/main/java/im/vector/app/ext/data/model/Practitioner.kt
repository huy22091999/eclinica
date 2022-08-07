package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Practitioner(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("patients")
    val patients: List<Patient>? = null,

    @SerializedName("encounters")
    val encounters: List<Encounter>? = null
) {
    override fun toString(): String {
        return displayName ?: ""
    }
}
