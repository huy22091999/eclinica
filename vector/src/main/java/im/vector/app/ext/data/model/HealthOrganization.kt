package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class HealthOrganization(
    val id: String? = null,
    val parentId: String? = null,
    val name: String? = null,
    val code: String? = null,
    val website: String? = null,
    val organizationType: Int? = null,
    val level: Int? = null,
//    val HealthOrganizationDto parent;
//    val List<HealthOrganizationDto> subOrganizations =  new ArrayList<HealthOrganizationDto>();
//    val AdministrativeUnitDto administrativeUnit;
    val shortDescription: String? = null,
    val longDescription: String? = null,
    var pageIndex: Int?  = null,
    var pageSize: Int?  = null,
) {
    override fun toString(): String {
        return name ?: "-"
    }
}
data class HealthOrgFilter(
    //types: PXN của PK và PXN liên kết
    @SerializedName("types")
    var types: List<Int>,
    @SerializedName("adminUnitCode")
    var adminUnitCode: String?,
)
