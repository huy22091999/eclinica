package im.vector.app.ext.registration

import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.*
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.demofragment.KeyboardUtils
import com.example.testapi.RealPathUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.hideKeyboardDrop
import im.vector.app.core.utils.*
import im.vector.app.databinding.GlobitsRegSection01Binding
import im.vector.app.ext.custom.AppCustom.Companion.transformIntoDatePicker
import im.vector.app.ext.custom.ExposedDropdownMenu
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.network.SessionManager
import im.vector.app.ext.data.type.CustomPair
import im.vector.app.ext.registration.list.AttachFildeAdapter
import im.vector.app.ext.utils.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class RegistrationSection0Fragment1 @Inject constructor() :
    RegistrationBaseFragment<GlobitsRegSection01Binding>(), AttachFildeAdapter.ItemListener {

    // prep treatment status
    private lateinit var txStatusAdapter: ArrayAdapter<CustomPair<Int>>
    private val txStatuses = mutableListOf<CustomPair<Int>>()

    private lateinit var editingPatient: Patient

    private lateinit var checkKeyboardUtils: KeyboardUtils

    //    private lateinit var patient: PatientInfo
    private lateinit var filedto: FiledtoItems


    companion object {
        var idd = ""
        var checkupload = false
        var fileList: ArrayList<FiledtoContent> = ArrayList()
        lateinit var attachFildeAdapter: AttachFildeAdapter
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection01Binding {
        return GlobitsRegSection01Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUI()
    }

    override fun onResume() {
        super.onResume()
        withState(viewModel) {
            it.editingPatient.let {
                viewModel.handle(
                    RegistrationActions.searchFileDto2(
                        Filedto(
                            1,
                            20,
                            it!!.id,
                            2
                        )
                    )
                )
                idd = it.id.toString()
            }
        }
    }

    override fun resetViewModel() {
    }

    fun nextTab() {
        RegistrationSection0Fragment.viewPager.setCurrentItem(1, true)
    }

    override fun updateWithState(state: RegistrationViewState) {
        when (state.asyncPatient) {
            is Success -> {
                // to make sure populate data for this screen is done only once
                if (!this::editingPatient.isInitialized) {
                    state.asyncPatient.invoke()?.let {
                        editingPatient = it
                        populateData()
                    }
                }
            }
            else -> Unit
        }



        if (state.asyncFileDto is Success) {
            if (!this::filedto.isInitialized) {
                state.asyncFileDto2.invoke()?.let {
                    filedto = it
                    populateFile()

                }
            }
        }
    }

    private fun populateFile() {
        with(filedto) {
            setDataRecyclerView(this.content)
        }
    }


    override fun updateData() {
        if (!this::editingPatient.isInitialized) {
            return
        }

        val curTxStatusPos = txStatuses.indexOfFirst { it.b == views.curTxStatus.text.toString() }

        if (views.curTxStatus.text.isNullOrEmpty()) {
            views.curTxStatus.requestFocus()
        }
        views.curTxStatus.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                views.curTxStatusTil.error = null

            }

        })

        with(editingPatient) {
            this.curTxStatus =
                if (curTxStatusPos >= 0) txStatusAdapter.getItem(curTxStatusPos)?.a else null
            // previous facility data
            if (this.curTxStatus == 2 ||  this.curTxStatus == 3) {
                this.clinicName = views.previousFacilityName.text.toString()
                this.txPeriodStartDate = views.txPeriodStartDate.parseDate()
                this.mostRecentVisitDate = views.latestEncounterDate.parseDate()
                this.nextAppointmentDate = views.nextAppointmentDate.parseDate()
                this.mostRecentHivTest = views.latestHivTestResult.text.toString()
                this.prepRegimen = views.prepRegimen.text.toString()
                this.drugsDispensed = views.drugsDispensed.text.toString().toIntOrNull()
            } else {
                this.clinicName = null
                this.txPeriodStartDate = null
                this.mostRecentVisitDate = null
                this.nextAppointmentDate = null
                this.mostRecentHivTest = null
                this.prepRegimen = null
                this.drugsDispensed = null
            }

            // support person
            this.supporterName = views.supportPersonFullname.text.toString()
            this.supporterAddress = views.supportPersonAddress.text.toString()
            this.supporterPhoneNumber = views.supportPersonPhoneNumber.text.toString()
        }

        viewModel.handle(RegistrationActions.UpdatePatientData(editingPatient))
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        views.sheetFilesView.layoutManager = layoutManager
        attachFildeAdapter = AttachFildeAdapter()

        views.sheetFilesView.adapter = attachFildeAdapter

        attachFildeAdapter.listener = this

    }

    private fun setDataRecyclerView(content: ArrayList<FiledtoContent>) {
        attachFildeAdapter.getData().clear()
        var data = content.filter { it.attackImageType == 5 }
        fileList = data as ArrayList<FiledtoContent>
        attachFildeAdapter.setData(fileList)
    }

    override fun onPause() {
        super.onPause()
        updateData()

    }

    ////////////////////////////// PRIVATE /////////////////////////////

    private fun initiateUI() {
        setupRecyclerView()
        views.uploadReferralSheet.setOnClickListener {
            uploadImages()
        }

        // treatment status
        txStatusAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, txStatuses)
        views.curTxStatus.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(txStatusAdapter)
        }
        updateTxStatuses()

        views.currentTxStatusDetails.visibility = View.GONE

        views.curTxStatus.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                views.currentTxStatusDetails.visibility =
                    if (position != 0) View.VISIBLE else View.GONE
                views.fileTranfer.visibility =
                    if (position == 2) View.VISIBLE else View.GONE
                customText(position, true)

            }

        val actionBarHeight = with(TypedValue().also {
            requireContext().theme.resolveAttribute(
                android.R.attr.actionBarSize,
                it,
                true
            )
        }) {
            TypedValue.complexToDimensionPixelSize(this.data, resources.displayMetrics)
        }

        checkKeyboardUtils = KeyboardUtils()
        checkKeyboardUtils.checkKeyBoard(requireActivity(), actionBarHeight, views.scrollview)

        views.txPeriodStartDate.transformIntoDatePicker(requireContext(), maxDate = Date())
        views.latestEncounterDate.transformIntoDatePicker(requireContext())
        views.nextAppointmentDate.transformIntoDatePicker((requireContext()))
        views.nextButton.setOnClickListener { nextTab() }
    }

    private fun customText(i: Int, c: Boolean) {
        val customText = SpannableString(txStatuses.get(i).toString())
        var last = txStatuses.get(i).toString().lastIndex
        if (c)
            customText.setSpan(RelativeSizeSpan(.1f), last - 4, last + 1, 0)
        else
            customText.removeSpan(RelativeSizeSpan(.1f))

        views.curTxStatus.setText(customText, false)
    }

    private fun updateTxStatuses() {
        txStatuses.clear()
        txStatuses.addAll(
            listOf(
                CustomPair(1, getString(R.string.tx_status_not_started)),
                CustomPair(2, getString(R.string.tx_status_on_treatment_at_facility)),
                CustomPair(3, getString(R.string.tx_status_transed_in)),
            )
        )
    }

    private fun populateData() {
        with(editingPatient) {
            val curTxStatusPos = txStatuses.indexOfFirst { it.a == this.curTxStatus }
            views.curTxStatus.setText(
                if (curTxStatusPos >= 0) txStatuses.get(curTxStatusPos).toString() else null,
                false
            )
            views.currentTxStatusDetails.visibility =
                if (curTxStatusPos > 0) View.VISIBLE else View.GONE
            views.fileTranfer.visibility =
                if (curTxStatusPos == 2) View.VISIBLE else View.GONE
            // transed in from another facility
            if (this.curTxStatus == 2||this.curTxStatus == 3) {
                views.previousFacilityName.setText(this.clinicName)
                views.txPeriodStartDate.setText(this.txPeriodStartDate?.format())
                if (this.txPeriodStartDate != null) {
                    views.txPeriodStartDate.transformIntoDatePicker(
                        requireContext(),
                        currentDate = this.txPeriodStartDate,
                        maxDate = Date()
                    )
                }
                views.latestEncounterDate.setText(this.mostRecentVisitDate?.format())
                if (this.mostRecentVisitDate != null) {
                    views.latestEncounterDate.transformIntoDatePicker(
                        requireContext(),
                        currentDate = this.mostRecentVisitDate
                    )
                }
                views.nextAppointmentDate.setText(this.nextAppointmentDate?.format())
                if (this.nextAppointmentDate != null) {
                    views.nextAppointmentDate.transformIntoDatePicker(
                        requireContext(),
                        currentDate = this.nextAppointmentDate
                    )
                }
                views.latestHivTestResult.setText(this.mostRecentHivTest)
                views.prepRegimen.setText(this.prepRegimen)
                views.drugsDispensed.setText(this.drugsDispensed?.toString())
            }

            // attached files

            // supporter
            views.supportPersonFullname.setText(this.supporterName)
            views.supportPersonPhoneNumber.setText(this.supporterPhoneNumber)
            views.supportPersonAddress.setText(this.supporterAddress)

            // lock edit
            if (profileStatus!! > 2) {
                views.curTxStatusTil.isEnabled = false
                views.drugsDispensedTil.isEnabled = false
                views.latestEncounterDateTil.isEnabled = false
                views.latestHivTestResultTil.isEnabled = false
                views.prepRegimenTil.isEnabled = false
                views.nextAppointmentDateTil.isEnabled = false
                views.txPeriodStartDateTil.isEnabled = false
                views.supportPersonPhoneNumberTil.isEnabled = false
                views.supportPersonAddressTil.isEnabled = false
                views.previousFacilityNameTil.isEnabled = false
                views.supportPersonFullnameTil.isEnabled = false
                views.uploadReferralSheet.isEnabled = false
                attachFildeAdapter.setOnlyView(false)
            }
        }
    }


    // upload attach file

    private val startAccessFileLauncher = registerForPermissionsResult { allGranted ->
        if (allGranted) {
            openGalleryForImage()
        } else {
            context?.toast(R.string.permissions_action_not_performed_missing_permissions)
        }
    }

    private var mActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.resultCode == Activity.RESULT_OK) {
            var data = result.data

            try {
                if (data != null) {
                    var uri = data.data

                    uploadFile(
                        uri!!,
                        //                                editingPatient.id.toString(),
                        idd,
                        "Đính kèm giấy chuyển tuyến",
                        "2",
                        "5"
                    )
                }
                RegistrationSection0Fragment.checkUpfileTab1 = true
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_push_file),
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun openGalleryForImage() {
        val intent = Intent()
        intent.type = "*/*"
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

        RegistrationSection0Fragment.checkclickUpload = true


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


    }

    override fun onItemClick(position: Int) {
    }

    override fun onItemDeleteFile(position: Int) {
        createDialog(
            requireContext(),
            "Xác nhận",
            false,
            attachFildeAdapter.getData().get(position).id.toString(),
            "delete", position
        )

    }

    override fun onItemDowFile(position: Int) {
        val token = SessionManager(requireContext()).fetchAuthToken()

        var url = RegistrationSection0Fragment3.URL_GETFILE.plus(
            attachFildeAdapter.getData().get(position).file!!.id
        )
        var title = URLUtil.guessFileName(url, null, null)

        var request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("Download file")
        request.setDescription("Downloading")
        var cookie = CookieManager.getInstance().getCookie(url)
        request.addRequestHeader("cookie", cookie)
        request.addRequestHeader("Authorization", "Bearer $token")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

        var downloadManager: DownloadManager =
            requireContext().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

    }

    override fun onItemEditFile(position: Int) {
        createDialog(
            requireContext(),
            "Nhập mô tả file đính kèm",
            true,
            attachFildeAdapter.getData().get(position).id.toString(),
            "put", position
        )
    }

    fun createDialog(
        context: Context,
        title: String?,
        edtenable: Boolean? = false,
        id: String,
        update: String,
        position: Int
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout._globits_update_delete_filedialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        var description=dialog.findViewById<MaterialTextView>(R.id.description)
        var btnClose = dialog.findViewById<Button>(R.id.filedialog_cancle)
        var btnAccept = dialog.findViewById<Button>(R.id.filedialog_ok)
        var txttitle = dialog.findViewById<MaterialTextView>(R.id.attachfile_title)
        var edtcontent = dialog.findViewById<TextInputEditText>(R.id.edt_update)
        txttitle.text = title.toString()
        if (edtenable == true) {
            description.visibility=View.GONE
            var edtcontentTil = dialog.findViewById<TextInputLayout>(R.id.edt_updatetil)
            edtcontentTil.visibility=View.VISIBLE
            edtcontentTil.isEnabled = true
            edtcontent.setText("")
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        btnAccept.setOnClickListener {
            if (update == "put") {
                var body = AttachFileUpdate(id, edtcontent.text.toString())
                viewModel.handle(RegistrationActions.updateAttachFile(body, id))
                attachFildeAdapter.getData().get(position).description = edtcontent.text.toString()

                Toast.makeText(requireContext(), "Đã cập nhập mô tả", Toast.LENGTH_SHORT).show()
            } else if (update == "delete") {
                viewModel.handle(RegistrationActions.deleteAttachFile(id))
                attachFildeAdapter.getData().removeAt(position)
                attachFildeAdapter.notifyItemRemoved(position)
                attachFildeAdapter.notifyItemRangeChanged(position, attachFildeAdapter.itemCount)

                Toast.makeText(requireContext(), "Đã xóa bản ghi", Toast.LENGTH_SHORT).show()

            }
            dialog.dismiss()

        }

        dialog.show()
    }

}
