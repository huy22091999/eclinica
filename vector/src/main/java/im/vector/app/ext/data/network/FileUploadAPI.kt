package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.model.LabTestOrderAttachment
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import javax.inject.Singleton

@Singleton
interface FileUploadAPI {


    @Multipart
    @POST("api/file/labTestOrderDocument/uploadattachment")
    fun uploadLabTestResultFile(
        @Part uploadfile: MultipartBody.Part
    ): Observable<LabTestOrderAttachment>


    @PUT("api/lab-test-order/{id}")
    fun updateOrder(@Path("id") id: String,@Body order: LabTestOrder): Observable<LabTestOrder>


}
