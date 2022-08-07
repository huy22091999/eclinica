package im.vector.app.ext.data

import android.content.Context
import dagger.Module
import dagger.Provides
import im.vector.app.ext.data.network.*
import im.vector.app.ext.data.repository.*

@Module
object ExtModule {

    @Provides
    fun providesRemoteDataSource(): RemoteDataSource {
        return RemoteDataSource()
    }

    @Provides
    fun providesAffiliatedLabApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): AffiliatedLabAPI {
        return remoteDataSource.buildApi(AffiliatedLabAPI::class.java, context)
    }

    @Provides
    fun providesAppointmentApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): AppointmentAPI {
        return remoteDataSource.buildApi(AppointmentAPI::class.java, context)
    }

    @Provides
    fun providesCommonDataApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): CommonDataAPI {
        return remoteDataSource.buildApi(CommonDataAPI::class.java, context)
    }

    @Provides
    fun providesDrugApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): DrugAPI {
        return remoteDataSource.buildApi(DrugAPI::class.java, context)
    }

    @Provides
    fun providesLabTestApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): LabTestAPI {
        return remoteDataSource.buildApi(LabTestAPI::class.java, context)
    }

    @Provides
    fun providesPatientApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): PatientAPI {
        return remoteDataSource.buildApi(PatientAPI::class.java, context)
    }

    @Provides
    fun providesUserApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): UserAPI {
        return remoteDataSource.buildApi(UserAPI::class.java, context)
    }

    @Provides
    fun providesHealthOrgApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): HealthOrgAPI {
        return remoteDataSource.buildApi(HealthOrgAPI::class.java, context)
    }

    @Provides
    fun provideFileUploadApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): FileUploadAPI {
        return remoteDataSource.buildApi(FileUploadAPI::class.java, context)
    }

    @Provides
    fun provideEncounterApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): EncounterAPI {
        return remoteDataSource.buildApi(EncounterAPI::class.java, context)
    }
    @Provides
    fun provideClinicApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): ClinicApi {
        return remoteDataSource.buildApi(ClinicApi::class.java, context)
    }

    @Provides
    fun providePrescriptionApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): PrescriptionAPI {
        return remoteDataSource.buildApi(PrescriptionAPI::class.java, context)
    }

    @Provides
    fun providesLaboratoryRepository(affiliatedLabAPI: AffiliatedLabAPI): LaboratoryRepository {
        return LaboratoryRepository(affiliatedLabAPI)
    }

    @Provides
    fun providesAppointmentRepository(api: AppointmentAPI): AppointmentRepository {
        return AppointmentRepository(api)
    }

    @Provides
    fun providesCommonDataRepository(api: CommonDataAPI): CommonDataRepository {
        return CommonDataRepository(api)
    }

    @Provides
    fun providesDrugRepository(api: DrugAPI): DrugRepository {
        return DrugRepository(api)
    }

    @Provides
    fun providesPatientRepository(api: PatientAPI): PatientRepository {
        return PatientRepository(api)
    }

    @Provides
    fun providesPrescriptionRepository(api: PrescriptionAPI): PrescriptionRepository {
        return PrescriptionRepository(api)
    }

    @Provides
    fun providesUserRepository(userAPI: UserAPI): UserRepository {
        return UserRepository(userAPI)
    }

    @Provides
    fun providesLabTestOrderRepository(api: LabTestAPI): LabTestOrderRepository {
        return LabTestOrderRepository(api)
    }

    @Provides
    fun providesHealthOrgRepository(api: HealthOrgAPI): HealthOrgRepository {
        return HealthOrgRepository(api)
    }

    @Provides
    fun providesLabTestRepository(api: LabTestAPI): LabTestRepository {
        return LabTestRepository(api)
    }

    @Provides
    fun providesFileUploadRepository(api: FileUploadAPI): FileUploadRepository {
        return FileUploadRepository(api)
    }
    @Provides
    fun providesClinicRepository(api: ClinicApi): ClinicRepository {
        return ClinicRepository(api)
    }

    @Provides
    fun providesEncounterRepository(encounterAPI: EncounterAPI): EncounterRepository {
        return EncounterRepository(encounterAPI)
    }

    @Provides
    fun providesPoliciesApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): PoliciesAPI {
        return remoteDataSource.buildApi(PoliciesAPI::class.java, context)
    }

    @Provides
    fun providesPoliciesRepository(api: PoliciesAPI): PoliciesRepository {
        return PoliciesRepository(api)
    }

}


