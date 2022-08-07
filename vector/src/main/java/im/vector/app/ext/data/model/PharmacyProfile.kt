package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class PharmacyProfile(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("contact")
    val contact: String? = null,

    @SerializedName("introduce")
    val intro: String? = null,

    @SerializedName("workingTime")
    val workingTime: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("prescriptions")
    val prescriptions: List<Prescription>? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
