package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class InformationResetPassword (
    @SerializedName("displayName") var displayName: String?=null,
    @SerializedName("email") var email : String?=null,
    @SerializedName("idNumber") var idNumber : String?=null

        )
