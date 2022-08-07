package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class EncounterPactitioner(

    @SerializedName("encounter")
    val encounter: Encounter? = null,

    @SerializedName("practitioner")
    val practitioner: Practitioner? = null,

    @SerializedName("roleType")
    val roleType: Int? = null
)
