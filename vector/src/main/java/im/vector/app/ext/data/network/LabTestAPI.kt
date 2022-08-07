package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.*
import io.reactivex.Observable
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface LabTestAPI {

    // Lab test dictionary
    @POST("api/labTest/searchByDto")
    fun queryList(@Body filter: QueryFilter): Observable<Page<LabTest>>

    @GET("api/labTest/{id}")
    fun queryOne(@Path("id") id: String): Observable<LabTest>

    // Lab test orders
    @POST("api/lab-test-order/current/searchByDto")
    fun queryListOrders(@Body filter: QueryFilter): Observable<Page<LabTestOrder>>

    @GET("api/lab-test-order/{id}")
    fun queryOneOrder(@Path("id") id: String): Observable<LabTestOrder>

    @GET("api/lab-test-order/getLabTestOrderByRecentDays")
    fun queryLatestOrder(): Observable<LabTestOrder>



    @PUT("api/lab-test-order/update/{id}")
    fun updateOneOrder(@Body order: LabTestOrder, @Path("id") id: String): Observable<LabTestOrder>

    @DELETE("api/lab-test-order/{id}")
    fun deleteOneOrder(@Path("id") id: String): Observable<Boolean>

    // Lab test order items
    @POST("api/labTestOrderItem/searchByDto")
    fun queryListOrderItems(@Body filter: QueryFilter): Observable<Page<LabTestOrderItem>>

    @GET("api/labTestOrderItem/{id}")
    fun queryOneOrderItem(@Path("id") id: String): Observable<LabTestOrderItem>

    @PUT("api/labTestOrderItem/{id}")
    fun updateOneOrderItem(@Body orderItem: LabTestOrderItem, @Path("id") id: String): Observable<LabTestOrderItem>

    @DELETE("api/labTestOrderItem/{id}")
    fun updateOneOrderItem(@Path("id") id: String): Observable<Boolean>

    // Lab test templates
    @POST("api/lab-test-temp/searchByDto")
    fun queryListTemplates(@Body filter: QueryFilter): Observable<Page<LabTestTemplate>>

    @GET("api/lab-test-temp/{id}")
    fun queryOneTemplate(@Path("id") id: String): Observable<LabTestTemplate>

}
