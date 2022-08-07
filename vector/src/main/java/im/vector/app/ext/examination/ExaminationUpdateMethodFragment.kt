package im.vector.app.ext.examination

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.databinding.GlobitsPrescriptionUpdateMethodBinding
import im.vector.app.ext.custom.ExposedDropdownMenu
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.CustomPair
import im.vector.app.ext.utils.snackbar
import javax.inject.Inject

class ExaminationUpdateMethodFragment @Inject constructor() : ExaminationBaseFragment<GlobitsPrescriptionUpdateMethodBinding>() {

    override var toolbarTitleRes: Int? = R.string.receive_type_drug

    companion object {
        const val TAG = "_PRESCRIPTION_UPDATE_METHOD_FRAGMENT"
    }
    private lateinit var prescription: Prescription

    private lateinit var receiveTypeAdapter: ArrayAdapter<CustomPair<Int>>
    private val receiveTypes = mutableListOf<CustomPair<Int>>()

    private val ExposedDropdownMenu.commonDropdownOptions: () -> Unit
        get() = {
            inputType = InputType.TYPE_NULL
            imeOptions = EditorInfo.IME_ACTION_DONE
            showSoftInputOnFocus = false
            onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                hideKeyboard(requireActivity())
            }
            onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                hideKeyboard(requireActivity())
            }
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

    private fun saveData() {
        // update data from views
        if (!this::prescription.isInitialized) {
            return
        }
        with(prescription) {
            val pos = receiveTypes.indexOfFirst { it.b == views.receiveType.text.toString() }
            this.receiveType = if (pos >= 0) receiveTypeAdapter.getItem(pos)?.a else null
            if (this.receiveType == 1) {
                this.receiveName = views.receiveName.text.toString()
                this.receivePhoneNumber = views.receivePhone.text.toString()
                this.receiveAddress = views.receiveAddress.text.toString()
            }
        }

        viewModel.handle(ExaminationAction.UpdateExamination(prescription))
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsPrescriptionUpdateMethodBinding {
        return GlobitsPrescriptionUpdateMethodBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
        setHasOptionsMenu(true)

        viewModel.observeViewEvents {
            handleViewEvents(it)
        }
    }

    private fun handleViewEvents(event: ExaminationViewEvents) {
        when (event) {
            is ExaminationViewEvents.UpdateExaminationComplete -> {
                withState(viewModel) {
                    when (it.asyncPrescription) {
                        is Success -> {
                            requireActivity().snackbar(getString(R.string.data_successfully_saved))
                        }
                        is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    private fun initiateUI() {
        // treatment status
        receiveTypeAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, receiveTypes)
        views.receiveType.apply {
            commonDropdownOptions
            setAdapter(receiveTypeAdapter)
        }
        updateReceiveType()

        views.layoutReceiveTypeHome.visibility = View.GONE
        views.receiveType.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            //nếu hình thức nhận tại nhà thì hiện
            if (receiveTypes[position].a == 1) {
                views.layoutReceiveTypeHome.visibility =  View.VISIBLE
                if (this::prescription.isInitialized) {
                    with(prescription) {
                        views.receiveName.setText(this.receiveName)
                        views.receiveAddress.setText(this.receiveAddress)
                        views.receivePhone.setText(this.receivePhoneNumber)
                    }
                }
            } else {
                views.layoutReceiveTypeHome.visibility =  View.GONE
            }
        }
    }

    private fun updateReceiveType() {
        receiveTypes.clear()
        receiveTypes.addAll(
            listOf(
                CustomPair(1, getString(R.string.receive_type_home)),
                CustomPair(3, getString(R.string.receive_type_drug_store)),
            )
        )
    }

    override fun updateWithState(state: ExaminationViewState) {
        if (state.asyncPrescription is Success) {
            state.asyncPrescription.invoke().let {
                prescription = it
                populateData()
            }
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun populateData() {
        if (!this::prescription.isInitialized) {
            return
        }

        with(prescription) {
            updateReceiveType()
            //nhận thuốc
            views.receivingUnitName.text = this.receivingUnitName ?: ""

            val receiveTypePos = receiveTypes.indexOfFirst { it.a == this.receiveType }
            views.receiveType.setText(
                if (receiveTypePos >= 0) receiveTypes[receiveTypePos].toString() else null,
                false
            )
            //nếu nhận tại nhà thì hiện thêm thông tin
            if (this.receiveType != null && this.receiveType == 1) {
                views.layoutReceiveTypeHome.visibility = View.VISIBLE
                views.receiveName.setText(this.receiveName)
                views.receiveAddress.setText(this.receiveAddress)
                views.receivePhone.setText(this.receivePhoneNumber)
            }
        }
    }
}
