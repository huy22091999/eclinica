package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Clinic(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("administrativeUnit")
    val administrativeUnit: AdminUnit? = null,

    @SerializedName("level")
    val level: Int? = null,

    @SerializedName("organizationType")
    val organizationType: Int? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("website")
    val website: String? = null,

    @SerializedName("url")
    val url:String?=null,

    @SerializedName("shortDescription")
    val shortDescription:String?=null,

    @SerializedName("longDescription")
    val longDescription:String?=null,

//    "subDepartments":null,"users":null,
//"parentId":null,"subOrganizations":[],
) {
    override fun toString(): String {
        return longDescription ?: ""
    }
}
