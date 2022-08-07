package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class TreatmentPeriodRegimen(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("treatmentPeriod")
    val treatmentPeriod: TreatmentPeriod? = null,

    @SerializedName("regimen")
    val regimen: Regimen? = null,

    @SerializedName("deliveryDate")
    val regimenSwitchDate: Date? = null,

    @SerializedName("reason")
    val reason: String? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
