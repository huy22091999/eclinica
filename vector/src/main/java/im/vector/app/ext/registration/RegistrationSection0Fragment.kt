package im.vector.app.ext.registration

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.view.MenuItemCompat
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.amulyakhare.textdrawable.TextDrawable
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import im.vector.app.R
import im.vector.app.core.extensions.isEmail
import im.vector.app.core.utils.toast
import im.vector.app.databinding.GlobitsRegSection0Binding
import im.vector.app.ext.data.model.Filedto
import im.vector.app.ext.data.model.FiledtoContent
import im.vector.app.ext.registration.RegistrationSection0Fragment1.Companion.attachFildeAdapter
import im.vector.app.ext.registration.RegistrationSection0Fragment1.Companion.fileList
import im.vector.app.ext.registration.custom.Notification
import im.vector.app.ext.registration.custom.RegistrationDialog
import im.vector.app.ext.utils.isNullOrEmptyAlt
import im.vector.app.ext.utils.onlyNumber
import im.vector.app.ext.utils.snackbar
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import timber.log.Timber
import java.lang.NullPointerException
import java.time.Year
import java.util.*
import javax.inject.Inject

class RegistrationSection0Fragment @Inject constructor() :
    RegistrationBaseFragment<GlobitsRegSection0Binding>() {

    private lateinit var editFormAdapter: RegistrationSection0PagerAdapter

    private var formHasError = false

    override var toolbarTitleRes: Int? = R.string.profile_information_title

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection0Binding {
        return GlobitsRegSection0Binding.inflate(inflater, container, false)
    }

    companion object {
        lateinit var viewPager: ViewPager2
        const val TAG = "_REGISTRATION_EDIT_PERSONAL_PROFILE"
        var checkinfo: Boolean = false

        var checkpatient = false
        var checkclickUpload = false

        var checkhasupFile = false

        var checkUpfileTab1 = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.handle(RegistrationActions.QueryPatientInfoData)

        setHasOptionsMenu(true)
        viewModel.observeViewEvents {
            handleViewEvents(it)
        }
        setupViewPager()
        populateData()

    }

    override fun resetViewModel() {
        viewModel.handle(RegistrationActions.ResetEditProfileData)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        withState(viewModel) {
            if (it.asyncPatient is Success) {
                it.asyncPatient.invoke()?.let {
                    if (it.profileStatus!! < 4) {
                        inflater.inflate(R.menu._globits_menu_save, menu)
                    }
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_save -> {
                hideKeyboard(requireActivity())
                checkpatient = true
                updateFromVisibleFragment()
                withState(viewModel) {
                    validateData(it)
                    saveData(it)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun updateData() {
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
        } else {
            super.onBackPressed(toolbarButton)
        }

        return false
    }

    ////////////////////////////// PRIVATE //////////////////////////

    // update data from visible fragment
    private fun updateFromVisibleFragment() {
        val fragment =
            editFormAdapter.getFragment(viewPager.currentItem) as RegistrationBaseFragment<*>
        fragment.updateData()
    }

    // validate
    private fun validateData(state: RegistrationViewState) {
        formHasError = false
        var calendar: Calendar = Calendar.getInstance()
        var file_tyep1 = arrayOf(3, 4)
        var file_tyep2 = arrayOf(1, 2, 3)
        state.editingPatient?.let {
            try {
                calendar.setTime(it.dob!!)
                if (state.asyncFileDto is Success) {
                    state.asyncFileDto.invoke()?.let { it1 ->
                        if (it1.content.size > 0) {
                            checkhasupFile = true
                            var data = it1.content.map { it.attackImageType }
                            if (Year.now().value - calendar.get(Calendar.YEAR) < 15) {

                                var c = data intersect file_tyep1.toList()
                                if (c.size > 1) checkinfo = true
                                else {
                                    requireActivity().snackbar("Bạn cần đính kèm đầy đủ giấy tờ tùy thân và chữ ký")
                                    viewPager.setCurrentItem(2, true)
                                    checkinfo = false
                                }
                            } else {

                                var c = data intersect file_tyep2.toList()
                                if (c.size > 2) checkinfo = true
                                else {
                                    viewPager.setCurrentItem(2, true)
                                    requireActivity().snackbar("Bạn cần đính kèm đầy đủ CMND mặt trước, CMND mặt sau và chữ ký")
                                    checkinfo = false

                                }
                            }

                        } else {
                            checkinfo = false
                        }

                    }
                }
            } catch (e: Exception) {

            }


            // on tab 2
            if (it.fullName.isNullOrEmptyAlt()
                || it.occupation == null
                || it.ethnicity == null
                || it.nationalIdNumber.isNullOrEmptyAlt()
                || it.email.isNullOrEmptyAlt()
                || it.email?.isEmail() == false
                || it.phoneNumber.isNullOrEmptyAlt()
                || it.serviceType == null
                || it.permCommune == null
                || it.curCommune == null
                || it.highRiskGroup == null
                || it.dob == null
                || it.gender == null
                || it.phoneNumber!!.length < 10
                || !it.phoneNumber.onlyNumber()
                || !it.nationalIdNumber.onlyNumber()
                || it.phoneNumber == it.nationalIdNumber
            ) {
                viewPager.setCurrentItem(1, true)
                formHasError = true
            }
            // on tab 1
            if (it.curTxStatus == null) {
                viewPager.setCurrentItem(0, true)
                formHasError = true
            }
            var hasToast = false

            try {
                if (it.phoneNumber!!.equals(it.nationalIdNumber)
                    && it.phoneNumber!!.isNotEmpty()
                    && it.nationalIdNumber!!.isNotEmpty()
                ) {
                    requireActivity().snackbar("Số điện thoại và CMND không được trùng nhau!")
                    hasToast = true
                }
                if (!hasToast && formHasError)
                    requireActivity().snackbar(getString(R.string.msg_please_complete_required_fields))
            } catch (e: Exception) {
            }


            if (!formHasError) {
                if (!checkhasupFile) {
                    requireActivity().snackbar("Bạn cần đính kèm file!")
                    viewPager.setCurrentItem(2, true)
                    formHasError = true
                }
                if (it.curTxStatus == 3 && fileList.size == 0) {
                    viewPager.setCurrentItem(0, true)
                    requireActivity().snackbar(getString(R.string.must_upload_file))
                    formHasError = true
                }
            }
        }

    }


    // save data
    private fun saveData(state: RegistrationViewState) {
        if (formHasError || !checkinfo) {
            return
        }
        state.editingPatient?.let {
            viewModel.handle(RegistrationActions.CheckDuplicateRegistration(it))
        }
    }

    private fun handleViewEvents(event: RegistrationViewEvents) {
        when (event) {
            is RegistrationViewEvents.SavePatientComplete -> {
                withState(viewModel) {
                    when (it.asyncEditingPatient) {
                        is Success -> {
                            Toast.makeText(
                                context,
                                getString(R.string.data_successfully_saved),
                                Toast.LENGTH_LONG
                            ).show()
                            createDialog(
                                requireContext(),
                                "THÔNG BÁO",
                                "Bạn đã cập nhập đủ thông tin cá nhân, mời bạn thực hiện trả lời câu hỏi sàng lọc"
                            )
                        }
                        is Fail -> Toast.makeText(
                            context,
                            getString(R.string.data_save_failure),
                            Toast.LENGTH_LONG
                        ).show()
                        else -> Unit
                    }
                }
            }
            is RegistrationViewEvents.CheckDuplicate -> {
                withState(viewModel) { state ->
                    when (state.asyncCheckDuplicate) {
                        is Success -> {
                            state.asyncCheckDuplicate.invoke().let {
                                if (!it?.isEmail!! && !it.isIdNumBer!! && !it.isPhoneNumber!!) {
                                    state.editingPatient?.let {
                                        Timber.e(
                                            "Cur status = %s, info updated = %s",
                                            it.curTxStatus.toString(),
                                            it.infoUpdated.toString()
                                        )
                                        if (it.curTxStatus == null && it.infoUpdated == false) {
                                            Timber.e("------------ here here -----------")
                                            it.infoUpdated = true
                                        }

                                        Timber.e("Info updated = %s", it.infoUpdated.toString())

                                        //profileStatus = 2 -> đã tải lên hồ sơ
                                        it.profileStatus = 2
                                        it.infoUpdated = true
                                        viewModel.handle(RegistrationActions.SaveEditingPatient(it))
                                    }
                                } else {
                                    it.description?.let { it1 -> requireActivity().snackbar(it1) }
                                }
                            }
                        }
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }

    }

    // set up the view pager
    private fun setupViewPager() {

        editFormAdapter = RegistrationSection0PagerAdapter(this)
        viewPager = views.pager
        viewPager.adapter = editFormAdapter
        viewPager.offscreenPageLimit = 3

        // tabs
        val tabNames = listOf<String>(
            getString(R.string.current_tx_status_label),
            getString(R.string.personal_information),
            getString(R.string.attached_documents)
        )

        val tabLayout = views.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames.get(position)
        }.attach()


    }

    // populate the initial data for the UI
    private fun populateData() {
//        viewModel.subscribe(this) {
//
//        }
    }

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)
        if (state.asyncPatientInfo is Success) {
            state.asyncPatientInfo.invoke()?.let {
                state.asyncAttachedFile?.invoke().let {
                    when (state.asyncAttachedFile) {
                        is Success -> {
                            viewModel.handle(
                                RegistrationActions.searchFileDto(
                                    Filedto(
                                        1,
                                        20,
                                        RegistrationSection0Fragment3.idd,
                                        1
                                    )
                                )
                            )
                            if (checkclickUpload) {
                                checkhasupFile = true
                                Toast.makeText(
                                    requireContext(),
                                    "Lưu thành công",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                if (it?.attackImageType == 5) {
                                    var file = FiledtoContent().apply {
                                        description = it.description
                                        file!!.name = it.file?.name
                                        attackImageType = 5
                                    }
                                    fileList.add(file)
                                    attachFildeAdapter.setData(fileList)
                                }
                            }
                            checkclickUpload = false
                        }
                        is Fail -> {
                            if (checkclickUpload)
                                Toast.makeText(
                                    requireContext(),
                                    "Có lỗi xảy ra khi lưu file",
                                    Toast.LENGTH_SHORT
                                ).show()
                            checkclickUpload = false

                        }
                    }
                }
            }
        }

    }


    fun createDialog(context: Context, title: String?, content: String?) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout._notifi_dialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        var btnClose = dialog.findViewById<Button>(R.id.dialog_oK)
        var txttitle = dialog.findViewById<MaterialTextView>(R.id.notifi_title)
        var txtcontent = dialog.findViewById<MaterialTextView>(R.id.notifi_content)
        txttitle.text = title.toString()
        txtcontent.text = content.toString()
        btnClose.setOnClickListener {
            dialog.dismiss()
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStack()
            }
            viewModel.handle(RegistrationActions.RequirePrepRegistration)

        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        checkpatient = false
    }


}
