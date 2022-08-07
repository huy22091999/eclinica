package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class LabTestTemplate(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("items")
    val items: List<LabTestTemplateItem>? = null,

    @SerializedName("correspondingTo")
    val correspondingTo: String? = null,
) {
    override fun toString(): String {
        return super.toString()
    }
}
