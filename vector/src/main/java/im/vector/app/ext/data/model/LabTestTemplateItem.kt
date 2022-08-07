package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class LabTestTemplateItem(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("template")
    val template: LabTestTemplate? = null,

    @SerializedName("labTest")
    val labTest: LabTest? = null,

    @SerializedName("compulsoryToDoNewTest")
    val required: Boolean? = null,

    @SerializedName("canRefuseTest")
    val optional: Boolean? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
