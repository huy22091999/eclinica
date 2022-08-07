package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Policies(
    @SerializedName("createDate") var createDate: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("modifyDate") var modifyDate: String? = null,
    @SerializedName("modifiedBy") var modifiedBy: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("voided") var voided: Boolean? = null,
    @SerializedName("code") var code: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("isActive") var isActive: Boolean? = null
)
{
    override fun toString(): String {
        return super.toString()
    }
}

