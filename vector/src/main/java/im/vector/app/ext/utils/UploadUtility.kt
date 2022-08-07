package im.vector.app.ext.utils

import android.app.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import im.vector.app.ext.data.model.LabTestOrderAttachment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.http.HTTP
import java.io.File

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UploadUtility(activity: Activity) {
    var activity = activity;
    var dialog: ProgressDialog? = null
    var serverURL: String = "https://demoprepclinic.globits.net:8053/telehealth/api/file/labTestOrderDocument/uploadattachment"
//    var serverUploadDirectoryPath: String = "https://handyopinion.com/tutorials/uploads/"


    @RequiresApi(Build.VERSION_CODES.R)
    fun uploadFile(sourceFileUri: Uri) {
        val pathFromUri = URIPathHelper().getPath(activity,sourceFileUri)
        val sourceFile = File(pathFromUri)
        Thread {
            val mimeType = getMimeType(sourceFile);
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread
            }
            toggleProgressDialog(true)
//            try {
//
//                val requestBody: RequestBody =
//                    MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addPart(sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
//                        .build()
//
//                val request: Request = Request.Builder().addHeader("Content-Type", "multipart/form-data")
//                    .url(serverURL).post(requestBody).build()
//
//                val response: Response = client.newCall(request).execute()
//                if (response.isSuccessful) {
//                    showToast("File uploaded successfully")
//                } else {
//                    Log.e("File upload", "failed")
//                    showToast("File uploading failed")
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//                Log.e("File upload", "failed")
//                showToast("File uploading failed")
//            }
            toggleProgressDialog(false)
        }.start()
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText( activity, message, Toast.LENGTH_LONG ).show()
        }
    }

    fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
            } else {
                dialog?.dismiss();
            }
        }
    }
}
