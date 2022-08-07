package im.vector.app.ext.registration

import android.media.MediaParser
import android.os.Build
import android.os.Bundle
import android.text.*
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.hideKeyboardDrop
import im.vector.app.databinding.GlobitsRegSection2Binding
import im.vector.app.ext.custom.AppCustom.Companion.transformIntoDatePicker
import im.vector.app.ext.custom.ExposedDropdownMenu
import im.vector.app.ext.data.model.AdminUnit
import im.vector.app.ext.data.model.HealthOrganization
import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.registration.custom.RegistrationDialog
import im.vector.app.ext.registration.list.SelectLabTestAdapter
import im.vector.app.ext.utils.*
import org.matrix.android.sdk.api.session.Session
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class RegistrationSection2Fragment @Inject constructor(
    private val session: Session
) : RegistrationBaseFragment<GlobitsRegSection2Binding>() {

    // provinces
    private lateinit var permProvinceAdapter: ArrayAdapter<AdminUnit>
    private lateinit var permHealthOrgAdapter: ArrayAdapter<HealthOrganization>
    private lateinit var labTestOrder: LabTestOrder
    private val provinces = mutableListOf<AdminUnit>()
    private val healthOrg = mutableListOf<HealthOrganization>()
    override var toolbarTitleRes: Int? = R.string.complete_lab_tests_title

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection2Binding {
        return GlobitsRegSection2Binding.inflate(inflater, container, false)
    }

    companion object {
        const val TAG = "_REGISTRATION_SELECT_LAB_TESTS"
    }

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)
        populateData(state)
        when (state.asyncLabTestOrder) {
            is Success -> {
                if (!this::labTestOrder.isInitialized) {
                    state.asyncLabTestOrder.invoke().let {
                        if (it != null) {
                            labTestOrder = it
                            setUpViews()
                        }
                    }
                }
            }
            else -> Unit
        }
        when (state.asyncEditingSelectLabTestOrder) {
            is Success -> {
                requireActivity().snackbar(getString(R.string.data_successfully_saved))
                RegistrationDialog.createDialog(
                    requireContext(),
                    "THÔNG BÁO",
                    "Cảm ơn bạn đã chọn dịch vụ xét nghiệm! Và chờ Phòng khám cập nhập kết quả",
                    this
                )
            }
            is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
            else -> Unit
        }

    }
    private fun getSpannedText(text: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }
    private fun setUpViews() {
        if (!this::labTestOrder.isInitialized || labTestOrder.id == null) {
            return
        }
        views.labtestExpectedDateTil.hint=getSpannedText(getString(R.string.labtest_expected_date))
        views.permProvinceTil.hint=getSpannedText(getString(R.string.province1))
        views.permHealthorgTil.hint=getSpannedText(getString(R.string.healthorg))
        if (labTestOrder.expectedDate != null) {
            views.labtestExpectedDate.setText(
                SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(
                    labTestOrder.expectedDate!!
                )
            )
            views.labtestExpectedDate.isEnabled=false
//            context?.let { views.labtestExpectedDate.transformIntoDatePicker(it, currentDate = labTestOrder.expectedDate) }
        }
        views.permHealthorg.setText(if (labTestOrder.receivingUnit != null) labTestOrder.receivingUnit.toString() else "")

        setupRecyclerView(labTestOrder)

    }

    override fun onResume() {
        super.onResume()
        // query common data
        viewModel.handle(RegistrationActions.QueryCommonDataLabTestEdit)
        viewModel.handle(RegistrationActions.ProvinceHealthOrgSelected(null))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initiateUI();
        formatdate(views.labtestExpectedDate,views.labtestExpectedDateTil)
//        viewModel.observeViewEvents {
//            handleViewEvents(it)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // menu save
        inflater.inflate(R.menu._globits_menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveData()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun populateData(state: RegistrationViewState) {
        if (state.asyncProvinces is Success) {
            state.asyncProvinces.invoke()?.let { updateProvinces(it) }
        }
        if (state.asyncHealthOrganization is Success) {
            state.asyncHealthOrganization.invoke()?.let {
                it.content?.let { healthOrgList ->
                    updateHealthOrg(healthOrgList)
                }
            }
        }
    }

//    private fun handleViewEvents(event: RegistrationViewEvents) {
//        when (event) {
//            is RegistrationViewEvents.SaveSelectTestServiceComplete -> {
//                withState(viewModel) {
//                    when (it.asyncEditingSelectLabTestOrder) {
//                        is Success -> {
//                            requireActivity().snackbar(getString(R.string.data_successfully_saved))
//                            RegistrationDialog.createDialog(
//                                requireContext(),
//                                "THÔNG BÁO",
//                                "Cảm ơn bạn đã chọn dịch vụ xét nghiệm! Và chờ Phòng khám cập nhập kết quả",
//                                this
//                            )
//                        }
//                        is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
//                        else -> Unit
//                    }
//                }
//            }
//            else -> Unit
//        }
//    }


    private fun updateProvinces(list: List<AdminUnit>) {
        provinces.clear()
        list.let { provinces.addAll(it) }
    }

    private fun updateHealthOrg(list: List<HealthOrganization>) {
        healthOrg.clear()
        list.let { healthOrg.addAll(it) }
    }

    override fun resetViewModel() {
    }

    override fun updateData() {
    }

    private fun initiateUI() {

        permHealthOrgAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, healthOrg)
        views.permHealthorg.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(permHealthOrgAdapter)
        }
        views.permHealthorg.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val org = healthOrg.getOrNull(position)
                if (org != null) {
                    views.permHealthorgTil.error = null
                }
            }

        // provinces
        permProvinceAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, provinces)
        views.permProvince.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(permProvinceAdapter)
        }
        views.permProvince.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val province = provinces.getOrNull(position)
                if (province?.code != null) {
                    viewModel.handle(RegistrationActions.ProvinceHealthOrgSelected(province.code))
                    views.permHealthorg.isEnabled = true
                    // clear selected districts and communes
                    views.permHealthorg.setText(null, false)
                }
            }

        views.labtestExpectedDate.transformIntoDatePicker(requireContext())

    }

    private fun setupRecyclerView(labTestOrder: LabTestOrder) {
        val layoutManager = LinearLayoutManager(context)
        views.labtestItemView.layoutManager = layoutManager
        val selectLabTestAdapter = labTestOrder.items?.let {
            activity?.let { it1 ->
                labTestOrder.patient?.serviceType?.let { it2 ->
                    SelectLabTestAdapter(
                        it2,
                        it,
                        it1,
                        requireContext()
                    )
                }
            }
        }
        if (selectLabTestAdapter != null) {
            views.labtestItemView.adapter = selectLabTestAdapter
        }
    }

    /////////////////////////// PRIVATE //////////////////////////////


    private fun validateData(data: LabTestOrder): Boolean {

        var check = true
        val pos = healthOrg.indexOfFirst { it.name == views.permHealthorg.text.toString() }
        data.receivingUnit = if (pos >= 0) healthOrg[pos] else null
        if (data.receivingUnit == null || views.permHealthorg.text.toString() == "") {
            views.permHealthorgTil.error = "Chưa chọn đơn vị xét nghiệm"
            check = false
        } else {
            views.permHealthorgTil.error = ""

        }
        val posvince = provinces.indexOfFirst { it.name == views.permProvince.text.toString() }
        var prov = if (posvince >= 0) provinces[posvince] else null
        if (prov == null || views.permProvince.text.toString() == "") {
            views.permProvinceTil.error = "Chưa chọn tỉnh"
            check = false

        } else {
            views.permProvinceTil.error = ""

        }

        var checkitem = false
        for (item in data.items!!) {
            if (item.labTestAndService == null)
                checkitem = true
        }
        if (checkitem) {
            views.txtservicetil.error = "Vui lòng tích hết phần chọn dịch vụ xét nghiệm"
            check = false

        } else {
            views.txtservicetil.error = ""
        }


        if (views.labtestExpectedDate.text.toString() == "") {
            views.labtestExpectedDateTil.error = "Trường này không được bỏ trống"

            check = false
        } else if (views.labtestExpectedDate.text.toString() != "") {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
            var nowdate = Calendar.getInstance().time
            var strDate: Date? =
                sdf.parse(String.format(views.labtestExpectedDate.text.toString()))!!
            var currentDate = sdf.format(nowdate)
            var t = sdf.parse(String.format(currentDate))
            if (!(strDate!!.after(t) || (strDate == t))) {
                views.labtestExpectedDateTil.error = "Không thể chọn ngày trước ngày hôm nay"
                check = false

            } else {
                views.labtestExpectedDateTil.error = ""
            }
        }
        if (!check)
            return false

        return true

    }

    private fun saveData() {
        // update data from views
        if (!this::labTestOrder.isInitialized) {
            return
        }

        with(labTestOrder) {
            val pos = healthOrg.indexOfFirst { it.name == views.permHealthorg.text.toString() }
            this.receivingUnit = if (pos >= 0) healthOrg[pos] else null
            if (!validateData(this)) {
                requireActivity().snackbar("Có trường bắt buộc chưa được nhập")
                return
            }
            viewModel.handle(RegistrationActions.SaveLabTest(this))
        }

    }

}
