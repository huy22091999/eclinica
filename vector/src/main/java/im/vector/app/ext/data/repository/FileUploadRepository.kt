package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.model.LabTestOrderAttachment
import im.vector.app.ext.data.model.Patient
import im.vector.app.ext.data.network.FileUploadAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUploadRepository@Inject constructor(
    private val api: FileUploadAPI
) {
        fun uploadLabTestResultFile(fileBody: MultipartBody.Part): Observable<LabTestOrderAttachment> =
        api.uploadLabTestResultFile(fileBody).subscribeOn(Schedulers.io())

        fun updateLabTest(id: String, order: LabTestOrder) : Observable<LabTestOrder> =
            api.updateOrder(id,order).subscribeOn(Schedulers.io())


}
