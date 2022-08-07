package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Filedto (
    @SerializedName("pageIndex" ) var pageIndex : Int?    = null,
    @SerializedName("pageSize"  ) var pageSize  : Int?    = null,
    @SerializedName("patientId" ) var patientId : String? = null,
    @SerializedName("type"      ) var type      : Int?    = null
)
