package im.vector.app.ext.registration

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.webkit.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.testapi.RealPathUtil
import im.vector.app.R
import im.vector.app.core.utils.PERMISSIONS_FOR_READING_FILES
import im.vector.app.core.utils.checkPermissions
import im.vector.app.core.utils.registerForPermissionsResult
import im.vector.app.core.utils.toast
import im.vector.app.databinding.GlobitsRegSection3Binding
import im.vector.app.ext.data.model.FileDescriptor
import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.model.LabTestOrderAttachment
import im.vector.app.ext.data.model.LabTestOrderItem
import im.vector.app.ext.data.network.SessionManager
import im.vector.app.ext.registration.custom.RegistrationDialog
import im.vector.app.ext.registration.list.LabTestResultAdapter
import im.vector.app.ext.registration.list.LabTestResultFileAdapter
import im.vector.app.ext.utils.URIPathHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import javax.inject.Inject


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RegistrationSection3Fragment @Inject constructor(
    private val session: Session
) : RegistrationBaseFragment<GlobitsRegSection3Binding>(), LabTestResultFileAdapter.LabItemListener {

    override var toolbarTitleRes: Int? = R.string.labtest_result
    //override var toolbarTitleRes: Int? = R.string.prep_registration_title
    private lateinit var labTestOrder: LabTestOrder
    private val REQUEST_CODE = 100
    private  lateinit var labTestResultFileAdapter: LabTestResultFileAdapter
    private var labFileList : ArrayList<LabTestOrderAttachment> = ArrayList()

    //    private  var LabUpdate :LabTestOrder = LabTestOrder()
    private  var documents : ArrayList<LabTestOrderAttachment> = ArrayList()



    private val startAccessFileLauncher = registerForPermissionsResult { allGranted ->
        if (allGranted) {
//            (viewModel.pendingAction as? RoomDetailAction.StartCall)?.let {
//                roomDetailViewModel.pendingAction = null
//                roomDetailViewModel.handle(it)
//            }
        } else {
            context?.toast(R.string.permissions_action_not_performed_missing_permissions)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu._globits_menu_save, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_save -> {
                viewModel.handle(RegistrationActions.updateLabTest(labTestOrder))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection3Binding {
        return GlobitsRegSection3Binding.inflate(inflater, container, false)
    }

    companion object {
        const val TAG = "_REGISTRATION_INPUT_LAB_RESULTS"
        var filename = ""
        var hasUpload = false

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(RegistrationActions.QueryCommonDataLabTestEdit)
        initUI()
        setHasOptionsMenu(true)

        setupRecyclerView()
    }

    private fun initUI() {
        views.uploadLabtestFile.setOnClickListener {
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
    }

    private fun openGalleryForImage() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_PICK)
        intent.type = "*/*"
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Timber.d(data.toString())
            try {
                data?.data?.let {
                    uploadFile(it)
                }
            }
            catch (e:Exception){
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_push_file),
                    Toast.LENGTH_SHORT
                ).show()
            }


            viewModel.handle(RegistrationActions.QueryCommonDataLabTestEdit)


            if (data != null) {
                Toast.makeText(context, activity?.let {
                    data.data?.let { it1 ->
                        URIPathHelper().getPath(
                            it,
                            it1
                        )
                    }
                }.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun uploadFile(sourceFileUri: Uri) {

        hasUpload = true

        var realpath =  RealPathUtil.getRealPath(requireContext(),sourceFileUri)

        var file = File(realpath)

        val fbody: RequestBody = file
            .asRequestBody(getMimeType(file)?.let { it.toMediaTypeOrNull() })

        var body = MultipartBody.Part.createFormData("uploadfile", file.name, fbody)

        filename =  file.name

        viewModel.handle(RegistrationActions.UploadLabTestResultFile(body))
    }


    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)
        when (state.asyncLabTestOrder) {
            is Success -> {
                if (!this::labTestOrder.isInitialized) {
                    state.asyncLabTestOrder.invoke().let {
                        if (it != null) {
                            labTestOrder = it
                            setDataRecyclerView(labTestOrder.attachments as ArrayList<LabTestOrderAttachment>)
                            populateData()
                        }
                    }
                }

            }
            else -> Unit
        }

        when(state.asyncFileAttachment){
            is Success -> {
                state.asyncFileAttachment.invoke().let {it1->
                    state.asyncAttachedFile?.invoke().let {
                        if (hasUpload) {
                            Toast.makeText(
                                requireContext(),
                                "Đính kèm file thành công ",
                                Toast.LENGTH_LONG
                            ).show()
                            var labfile =it1!!
                            labFileList.add(labfile)

                            //labTestOrder.attachments!!.add(labfile)
                            labTestResultFileAdapter.setData(labFileList)
                        }
                    }
                }
                hasUpload = false

            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Có lỗi xảy ra khi lưu file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when(state.asyncEditingSelectLabTestOrder){
            is Success ->
                RegistrationDialog.createDialog(requireContext(),"THÔNG BÁO",
                    "Đã cập nhập kết quả xét nghiệm từ phía bạn! Vui lòng chờ phòng khám cập nhập kết quả",this)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun populateData() {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val orderDate: String = formatter.format(labTestOrder.orderDate!!)
        String.format("%s", orderDate)
            .also { views.labtestRequestDate.text = it }
    }

    private fun setupRecyclerView() {
        //list file attachment
        labTestResultFileAdapter = LabTestResultFileAdapter()

        views.labtestFilesView.layoutManager = LinearLayoutManager(context)
        views.labtestFilesView.setHasFixedSize(true)

        views.labtestFilesView.adapter = labTestResultFileAdapter
        labTestResultFileAdapter.listener = this
    }

    private fun setDataRecyclerView(content: ArrayList<LabTestOrderAttachment>) {
        views.labtestItemView.layoutManager = LinearLayoutManager(context)
        views.labtestItemView.setHasFixedSize(true)
        views.labtestItemView.adapter =
            LabTestResultAdapter(labTestOrder.items as ArrayList<LabTestOrderItem>)

        labFileList  = content

        labTestResultFileAdapter.getData().clear()

        labTestResultFileAdapter.setData(labFileList )
    }


    override fun resetViewModel() {

    }

    override fun updateData() {

    }

    override fun onLabItemDeleteFile(position: Int) {
        createDialog(
            requireContext(),
            position
        )
    }

    override fun onLabItemDowFile(position: Int) {
        val token = SessionManager(requireContext()).fetchAuthToken()

        var url = RegistrationSection0Fragment3.URL_GETFILE.plus(
            labTestResultFileAdapter.getData().get(position).file!!.id
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
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

    }

    override fun onLabItemDetailFile(position: Int) {
        if (labTestResultFileAdapter.getData().get(position).file!!.contentType == "image/png" ||
            labTestResultFileAdapter.getData().get(position).file!!.contentType == "image/jpeg")
            createImageDialog(requireContext(),position)
        else
            Toast.makeText(requireContext(),"Định dạng tệp tin không thể xem. Hãy tải xuống",Toast.LENGTH_SHORT).show()
    }


    /////////////////////////// PRIVATE //////////////////////////////

    fun createDialog(
        context: Context,
        position: Int
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout._globits_update_delete_filedialog)

        var btnClose = dialog.findViewById<Button>(R.id.filedialog_cancle)
        var btnAccept = dialog.findViewById<Button>(R.id.filedialog_ok)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        btnAccept.setOnClickListener {
            print(labTestOrder)
            labTestResultFileAdapter.getData().removeAt(position)
            labTestResultFileAdapter.notifyItemRemoved(position)
            labTestResultFileAdapter.notifyItemRangeChanged(position,labTestResultFileAdapter.itemCount)
            dialog.dismiss()
        }
        dialog.show()
    }


    fun createImageDialog( context: Context,position: Int){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout._globits_labtestfile_detaildialog)
        var btnClose = dialog.findViewById<Button>(R.id.labtestfiledialog_cancle)
        var fileImage = dialog.findViewById<AppCompatImageView>(R.id.labtestfiledetail)
        val token = SessionManager(requireContext()).fetchAuthToken()
        var glideUrl = GlideUrl(
            RegistrationSection0Fragment3.URL_GETFILE.plus(labTestResultFileAdapter.getData().get(position).file!!.id
            ),
            LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )

        Glide
            .with(requireContext())
            .load(glideUrl)
            .centerCrop()
            .into(
                fileImage
            )
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


}
