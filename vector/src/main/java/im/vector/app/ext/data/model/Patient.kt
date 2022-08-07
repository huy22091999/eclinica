package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import im.vector.app.ext.data.type.Gender
import im.vector.app.ext.utils.isNullOrEmptyAlt
import org.matrix.android.sdk.api.util.MatrixItem
import java.util.*

data class Patient(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("firstName")
    var firstName: String? = null,

    @SerializedName("lastName")
    var lastName: String? = null,

    @SerializedName("displayName")
    var fullName: String? = null,

    @SerializedName("birthDate")
    var dob: Date? = null,

    @SerializedName("gender")
    var gender: Gender? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,

    @SerializedName("idNumber")
    var nationalIdNumber: String? = null,

    // Current PrEP status
    @SerializedName("currentTreatmentStatus")
    var curTxStatus: Int? = null,

    // 0 = chưa từng điều trị, 1 đang điều trị, 2 đã dừng điều trị
    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("profileStatus")
    var profileStatus: Int? = null,

    @SerializedName("profileStatusRequired")
    var extraInfoNeeded: String? = null,

    @SerializedName("code")
    var code: String? = null,

    @SerializedName("userName")
    var username: String? = null,

    @SerializedName("occupation")
    var occupation: Occupation? = null,

    @SerializedName("ethnicity")
    var ethnicity: Ethnicity? = null,

    @SerializedName("serviceType")
    var serviceType: Int? = null,

    @SerializedName("clientResourceType")
    var clientType: Int? = null, // Nguồn khách hàng

    @SerializedName("receptionDate")
    var receptionDate: Date? = null,

    @SerializedName("practitioners")
    var practitioners: List<PatientPractitioner>? = null,

    @SerializedName("treatmentPeriods")
    var treatmentPeriods: List<TreatmentPeriod>? = null,

    @SerializedName("currentResidence")
    var curCommune: AdminUnit? = null,

    @SerializedName("permanentResidence")
    var permCommune: AdminUnit? = null,

    @SerializedName("currentDetailResidence")
    var curStreetAddress: String? = null,

    @SerializedName("permanentDetailResidence")
    var permStreetAddress: String? = null,

    @SerializedName("newUpdate")
    var infoUpdated: Boolean? = null,

    @SerializedName("healthInsuranceNumber")
    var shiNumber: String? = null,

    @SerializedName("pregnant")
    var pregnant: Boolean? = null,

    @SerializedName("registrationDate")
    var registrationDate: Date? = null,

    // in case of referred in

    @SerializedName("clinicName")
    var clinicName: String? = null,

    @SerializedName("startDateOfTreatment")
    var txPeriodStartDate: Date? = null,

    @SerializedName("lastVisit")
    var mostRecentVisitDate: Date? = null,

    @SerializedName("mostRecentHivTestResults")
    var mostRecentHivTest: String? = null,

    @SerializedName("prepRegimen")
    var prepRegimen: String? = null,

    @SerializedName("numberOfMedicinesDispensed")
    var drugsDispensed: Int? = null,

    @SerializedName("nextAppointmentDate")
    var nextAppointmentDate: Date? = null,

    // Files
    @SerializedName("patientMedicalRecordFiles")
    var attachedDocs: List<AttachedFile>? = null, // CCCD/CMTND, Medical records, etc.

    @SerializedName("transitPaperFiles")
    var referralDocs: List<AttachedFile>? = null,

    // Supporter

    @SerializedName("supporterName")
    var supporterName: String? = null,

    @SerializedName("supporterAddress")
    var supporterAddress: String? = null,

    @SerializedName("supporterPhoneNumber")
    var supporterPhoneNumber: String? = null,

    @SerializedName("highRiskGroup")
    var highRiskGroup: Int? = null
) {
    override fun toString(): String {
        return fullName ?: "-"
    }

    fun toMatrixItem(userId: String) = MatrixItem.UserItem(userId, fullName, null)

    fun isSameAddress(): Boolean {
        if (permCommune == null
            && curCommune == null
            && permStreetAddress.isNullOrEmptyAlt()
            && curStreetAddress.isNullOrEmptyAlt()
        ) {
            return true
        }

        return permCommune != null
                && curCommune != null
                && permCommune?.id == curCommune?.id
                && ((permStreetAddress.isNullOrEmptyAlt() && curStreetAddress.isNullOrEmptyAlt())
                || (!permStreetAddress.isNullOrEmptyAlt() && permStreetAddress.equals(curStreetAddress, true)))
    }
}
