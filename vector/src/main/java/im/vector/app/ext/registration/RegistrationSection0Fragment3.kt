package im.vector.app.ext.registration

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResultListener
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.testapi.RealPathUtil
import im.vector.app.R
import im.vector.app.core.utils.PERMISSIONS_FOR_READING_FILES
import im.vector.app.core.utils.checkPermissions
import im.vector.app.core.utils.registerForPermissionsResult
import im.vector.app.core.utils.toast
import im.vector.app.databinding.GlobitsRegSection03Binding
import im.vector.app.ext.data.model.Filedto
import im.vector.app.ext.data.model.FiledtoItems
import im.vector.app.ext.data.model.PatientInfo
import im.vector.app.ext.data.network.SessionManager
import im.vector.app.ext.registration.RegistrationSection0Fragment.Companion.checkclickUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import javax.inject.Inject


class RegistrationSection0Fragment3 @Inject constructor() :
    RegistrationBaseFragment<GlobitsRegSection03Binding>() {

    private val REQUEST_CODE = 101

    //    private lateinit var attachfile: PatientMedicalRecordFile
    private lateinit var mUri: Uri

    //private lateinit var patient: PatientInfo
    private lateinit var filedto: FiledtoItems


    companion object {
        var description: String = ""
        var attackImageType: String = ""

        val URL_GETFILE =
            "https://demoprepclient.globits.net:8053/telehealth/api/fileDownload/document/"
        var idd = ""

    }

    private var age = 0

    private val startAccessFileLauncher = registerForPermissionsResult { allGranted ->
        if (allGranted) {
            openGalleryForImage()
        } else {
            context?.toast(R.string.permissions_action_not_performed_missing_permissions)
        }
    }

    override fun onResume() {
        super.onResume()
        setFragmentResultListener("requestKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            try {

                val result = bundle.getString("bundleKey")
                val dob = SimpleDateFormat("dd/MM/yyyy").parse(result!!)
                var dob_year = LocalDate.parse(SimpleDateFormat("yyyy-MM-dd").format(dob!!)).year
                age = Year.now().value - dob_year


                if (age < 15) {
                    views.uploadfile1.visibility = View.VISIBLE
                    views.uploadfile2.visibility = View.GONE
                    views.uploadfile3.visibility = View.GONE
                } else {
                    views.uploadfile3.visibility = View.VISIBLE
                    views.uploadfile2.visibility = View.VISIBLE
                    views.uploadfile1.visibility = View.GONE

                }
            } catch (e: Exception) {
                Log.e("Exception Fragment3", e.message.toString())
            }

        }

        withState(viewModel) {
            it.editingPatient.let {
                viewModel.handle(
                    RegistrationActions.searchFileDto(
                        Filedto(
                            1,
                            20,
                            it!!.id,

                            1
                        )
                    )

                )
                idd = it.id.toString()
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.handle(RegistrationActions.QueryPatientInfoData)
        withState(viewModel) { it1 ->
            it1.editingPatient.let {
                if (it?.profileStatus!! < 4) {

                    views.btnupload1.setOnClickListener {
                        description = views.txtUploadfile1.text.toString()
                        attackImageType = RecordFile.IDENTITY_DOCUMENTS.id
                        uploadImages()
                    }
                    views.btnupload2.setOnClickListener {
                        description = views.txtUploadfile2.text.toString()
                        attackImageType = RecordFile.IDCART_FRONT.id
                        uploadImages()
                    }
                    views.btnupload3.setOnClickListener {
                        description = views.txtUploadfile3.text.toString()
                        attackImageType = RecordFile.IDCART_BACK.id
                        uploadImages()
                    }

                    views.btnupload4.setOnClickListener {
                        description = views.txtUploadfile4.text.toString()
                        attackImageType = RecordFile.HAND_SIGN.id
                        uploadImages()

                    }

                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private var mActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.resultCode == Activity.RESULT_OK) {
            var data = result.data
            if (data != null) {
                var uri = data.data
                if (!::mUri.isInitialized) {
                    mUri = uri!!

                }

                withState(viewModel) {
                    it.editingPatient.let {
                        if (it != null) {
                            uploadFile(
                                uri!!,
                                it.id.toString(),
                                description,
                                "1",
                                attackImageType
                            )
                        }
                    }
                }
                lateinit var source:ImageDecoder.Source
                lateinit var bitmap:Bitmap
                try {
                    source = ImageDecoder.createSource(requireContext().contentResolver, uri!!)
                     bitmap = ImageDecoder.decodeBitmap(source)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_push_file),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (attackImageType == "1") {
                    views.btnupload2.setImageBitmap(bitmap)
                    showImage(2)

                } else if (attackImageType == "2") {
                    views.btnupload3.setImageBitmap(bitmap)
                    showImage(3)

                } else if (attackImageType == "3") {
                    views.btnupload4.setImageBitmap(bitmap)
                    showImage(4)

                } else if (attackImageType == "4") {
                    views.btnupload1.setImageBitmap(bitmap)
                    showImage(1)

                }


            }


        }
    }

    private fun uploadImages() {

        if (checkPermissions(
                PERMISSIONS_FOR_READING_FILES,
                this.requireActivity(),
                startAccessFileLauncher,
                R.string.permissions_rationale_msg_storage
            )
        ) {
            openGalleryForImage()
        }
    }

    @SuppressLint("NewApi")
    private fun openGalleryForImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        mActivityResult.launch(Intent.createChooser(intent, "pick file"))
    }

    fun getMimeType(file: File): String {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type!!
    }


    fun uploadFile(
        sourceFileUri: Uri,
        patient_id: String,
        description: String,
        type: String,
        attackImageType: String
    ) {
        try {
            checkclickUpload = true
            val pathFromUri = RealPathUtil.getRealPath(requireContext(), sourceFileUri)
            var file = File(pathFromUri!!)
            if (getMimeType(file) == "image/*")
                file = RealPathUtil.saveBitmapToFile(file)!!

            val fbody: RequestBody =
                file.asRequestBody(getMimeType(file).let { it.toMediaTypeOrNull() })

            val body: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadfile", file.name, fbody)
                .addFormDataPart("patient_id", patient_id)
                .addFormDataPart("description", description)
                .addFormDataPart("type", type)
                .addFormDataPart("attackImageType", attackImageType)
                .build()

            viewModel.handle(
                RegistrationActions.UploadImage(
                    body
                )
            )
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                getString(R.string.error_push_file),
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection03Binding {
        return GlobitsRegSection03Binding.inflate(inflater, container, false)
    }

    override fun resetViewModel() {
    }

    override fun updateData() {

    }

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)


        if (state.asyncFileDto is Success) {
            if (!this::filedto.isInitialized) {
                state.asyncFileDto.invoke()?.let {
                    filedto = it
                    populateData()

                }
            }
        }
    }


    private fun showImage(i: Int?) {
        when (i) {
            1 -> {
                views.iconupload1.visibility = View.GONE
                views.txtUploadfile1.visibility = View.GONE
            }

            2 -> {
                views.iconupload2.visibility = View.GONE
                views.txtUploadfile2.visibility = View.GONE
            }

            3 -> {
                views.iconupload3.visibility = View.GONE
                views.txtUploadfile3.visibility = View.GONE
            }

            4 -> {
                views.iconupload4.visibility = View.GONE
                views.txtUploadfile4.visibility = View.GONE
            }

            else -> {
                views.iconupload1.visibility = View.GONE
                views.txtUploadfile1.visibility = View.GONE

                views.iconupload2.visibility = View.GONE
                views.txtUploadfile2.visibility = View.GONE

                views.iconupload3.visibility = View.GONE
                views.txtUploadfile3.visibility = View.GONE

                views.iconupload4.visibility = View.GONE
                views.txtUploadfile4.visibility = View.GONE
            }
        }
    }


    private fun populateData() {
        with(filedto) {
            val token = SessionManager(requireContext()).fetchAuthToken()
            if (this.content.size > 0) {

                showImage(-1)
                for (data in this.content) {
                    var glideUrl = GlideUrl(
                        URL_GETFILE.plus(data.file!!.id),
                        LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                    )
                    if (data.attackImageType == 1)
                        Glide
                            .with(requireContext())
                            .load(glideUrl)
                            .centerCrop()
                            .into(
                                views.btnupload2
                            )
                    if (data.attackImageType == 2)
                        Glide
                            .with(requireContext())
                            .load(glideUrl)
                            .centerCrop()
                            .into(
                                views.btnupload3
                            )
                    if (data.attackImageType == 3)
                        Glide
                            .with(requireContext())
                            .load(glideUrl)
                            .centerCrop()
                            .into(
                                views.btnupload4
                            )

                    if (data.attackImageType == 4)
                        Glide
                            .with(requireContext())
                            .load(glideUrl)
                            .centerCrop()
                            .into(
                                views.btnupload1
                            )
                }
            }

        }
    }


    enum class RecordFile(val id: String, val des: String) {
        MEDICAL_RECORD("1", ""), // HỒ SƠ BỆNH ÁN
        TRANSIT_PAPER_FILES("2", ""), // GIẤY CHUYỂN TUYẾN
        IDCART_FRONT("1", "CMND MẶT TRƯỚC"), /// CMND MẶT TRƯỚC
        IDCART_BACK("2", "CMND MẶT SAU"), // CMND MẶT SAU
        HAND_SIGN("3", "CHỮ KÝ"), // CHỮ KÝ
        IDENTITY_DOCUMENTS(
            "4",
            "GIẤY TỜ TÙY THÂN KHI DƯỚI 15 tuổi"
        ) // GIẤY TỜ TÙY THÂN KHI DƯỚI 15 tuổi
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}



