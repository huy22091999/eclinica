package im.vector.app.ext.profile

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.google.android.material.textfield.TextInputEditText
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.databinding.GlobitsProfileEditSection0Binding
import im.vector.app.ext.custom.AppCustom.Companion.transformIntoDatePicker
import im.vector.app.ext.custom.ExposedDropdownMenu
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AddressType
import im.vector.app.ext.data.type.CustomPair
import im.vector.app.ext.data.type.Gender
import im.vector.app.ext.registration.RegistrationActions
import im.vector.app.ext.registration.RegistrationViewEvents
import im.vector.app.ext.registration.RegistrationViewState
import im.vector.app.ext.utils.*
import java.util.Date
import javax.inject.Inject

class EditProfileSection0Fragment @Inject constructor(): EditProfileBaseFragment<GlobitsProfileEditSection0Binding>() {

    private lateinit var genderAdapter: ArrayAdapter<CustomPair<Gender>>
    private val genders = mutableListOf<CustomPair<Gender>>()

    private lateinit var serviceTypeAdapter: ArrayAdapter<CustomPair<Int>>
    private val serviceTypes = mutableListOf<CustomPair<Int>>()

    // ethnics
    private lateinit var ethnicAdapter: ArrayAdapter<Ethnicity>
    private val ethnics = mutableListOf<Ethnicity>()

    // occupations
    private lateinit var occupationAdapter: ArrayAdapter<Occupation>
    private val occupations = mutableListOf<Occupation>()

    // provinces
    private lateinit var permProvinceAdapter: ArrayAdapter<AdminUnit>
    private lateinit var curProvinceAdapter: ArrayAdapter<AdminUnit>
    private val provinces = mutableListOf<AdminUnit>()

    // districts
    private lateinit var permDistrictAdapter: ArrayAdapter<AdminUnit>
    private lateinit var curDistrictAdapter: ArrayAdapter<AdminUnit>
    private val permDistricts = mutableListOf<AdminUnit>()
    private val curDistricts = mutableListOf<AdminUnit>()

    // communes
    private lateinit var permCommuneAdapter: ArrayAdapter<AdminUnit>
    private lateinit var curCommuneAdapter: ArrayAdapter<AdminUnit>
    private val permCommunes = mutableListOf<AdminUnit>()
    private val curCommunes = mutableListOf<AdminUnit>()

    private val selPermCommunes = mutableListOf<AdminUnit>()
    private val selCurCommunes = mutableListOf<AdminUnit>()

    // editing patient
    private lateinit var editingPatient: Patient


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

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsProfileEditSection0Binding {
        return GlobitsProfileEditSection0Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        viewModel.observeViewEvents {
            handleViewEvents(it)
        }
    }

    override fun updateWithState(state: ProfileViewState) {
        super.updateWithState(state)
        populateData(state)
    }

    override fun updateData() {
        if (!this::editingPatient.isInitialized) {
            return
        }

        with(editingPatient) {
            this.fullName = views.fullname.toStringAlt()
            this.dob = views.dob.parseDate()

            var pos = genders.indexOfFirst { it.b == views.gender.text.toString() }
            this.gender = if (pos >= 0) genderAdapter.getItem(pos)?.a else null

            this.pregnant = views.chkPregnant.isChecked

            pos = occupations.indexOfFirst { it.name == views.occupation.text.toString() }
            this.occupation = if (pos >= 0) occupationAdapter.getItem(pos) else null

            pos = ethnics.indexOfFirst { it.name == views.ethnic.text.toString() }
            this.ethnicity = if (pos >= 0) ethnicAdapter.getItem(pos) else null

            this.nationalIdNumber = views.nationalId.toStringAlt()
            this.email = views.emailAddress.toStringAlt()
            this.phoneNumber = views.phoneNumber.toStringAlt()
            this.shiNumber = views.shi.toStringAlt()

            pos = serviceTypes.indexOfFirst { it.b == views.serviceType.text.toString() }
            this.serviceType = if (pos >= 0) serviceTypeAdapter.getItem(pos)?.a else null

            // perm address
            pos = selPermCommunes.indexOfFirst { it.name == views.permCommune.text.toString() }
            this.permCommune = if (pos >= 0) permCommuneAdapter.getItem(pos) else null
            this.permStreetAddress = views.permStreetAddress.toStringAlt()

            // cur address
            pos = selCurCommunes.indexOfFirst { it.name == views.curCommune.text.toString() }
            this.curCommune = if (pos >= 0) curCommuneAdapter.getItem(pos) else null
            this.curStreetAddress = views.curStreetAddress.toStringAlt()
        }
    }

    override fun onPause() {
        super.onPause()
            updateData()
    }

    private fun initUI() {
        viewModel.handle(ProfileAction.QueryCommonData4ProfileEdit)
        genderAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, genders)

        views.gender.apply {
            commonDropdownOptions
            setAdapter(genderAdapter)
        }
        updateGenders()

        serviceTypeAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, serviceTypes)
        views.serviceType.apply {
            commonDropdownOptions
            setAdapter(serviceTypeAdapter)
        }
        updateServiceTypes()

        // occupations
        occupationAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, occupations)
        views.occupation.apply {
            commonDropdownOptions
            setAdapter(occupationAdapter)
        }

        // ethnics
        ethnicAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, ethnics)
        views.ethnic.apply {
            commonDropdownOptions
            setAdapter(ethnicAdapter)
        }

//        views.chkSameAddress.setOnCheckedChangeListener { _, checked ->
////            views.curAddressTil.visible(!checked)
//        }

        // provinces
        permProvinceAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, provinces)
        views.permProvince.apply {
            commonDropdownOptions
            setAdapter(permProvinceAdapter)
        }

        curProvinceAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, provinces)
        views.curProvince.apply {
            commonDropdownOptions
            setAdapter(curProvinceAdapter)
        }

        views.permProvince.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val province = provinces.getOrNull(position)
            if (province?.id != null) {
                viewModel.handle(ProfileAction.ProvinceSelected(province.id, AddressType.PERM))

                // clear selected districts and communes
                views.permDistrict.setText(null, false)
                views.permCommune.setText(null, false)

                if (views.chkSameAddress.isChecked) {
                    views.curProvince.setText(province.name, false)
                    viewModel.handle(ProfileAction.ProvinceSelected(province.id, AddressType.CUR))

                    views.curDistrict.setText(null, false)
                    views.curCommune.setText(null, false)
                }
            }
        }
        views.curProvince.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val province = provinces.getOrNull(position)
            if (province?.id != null) {
                viewModel.handle(ProfileAction.ProvinceSelected(province.id, AddressType.CUR))

                // clear selected districts and communes
                views.curDistrict.setText(null, false)
                views.curCommune.setText(null, false)
            }
        }

        // districts
        permDistrictAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, permDistricts)
        views.permDistrict.apply {
            commonDropdownOptions
            setAdapter(permDistrictAdapter)
        }

        curDistrictAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, curDistricts)
        views.curDistrict.apply {
            commonDropdownOptions
            setAdapter(curDistrictAdapter)
        }

        views.permDistrict.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val district = permDistricts.getOrNull(position)
            if (district?.id != null) {
                viewModel.handle(ProfileAction.DistrictSelected(district.id, AddressType.PERM))

                // clear selected communes
                views.permCommune.setText(null, false)

                if (views.chkSameAddress.isChecked) {
                    views.curDistrict.setText(district.name, false)
                    viewModel.handle(ProfileAction.DistrictSelected(district.id, AddressType.CUR))

                    views.curCommune.setText(null, false)
                }
            }
        }

        views.curDistrict.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val district = curDistricts.getOrNull(position)
            if (district?.id != null) {
                viewModel.handle(ProfileAction.DistrictSelected(district.id, AddressType.CUR))

                // clear selected communes
                views.curCommune.setText(null, false)
            }
        }

        // communes
        permCommuneAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, permCommunes)
        views.permCommune.apply {
            commonDropdownOptions
            setAdapter(permCommuneAdapter)
        }

        views.permCommune.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val commune = permCommunes.getOrNull(position)
            if (commune?.id != null && views.chkSameAddress.isChecked) {
                views.curCommune.setText(commune.name, false)
            }
        }

        curCommuneAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, curCommunes)
        views.curCommune.apply {
            commonDropdownOptions
            setAdapter(curCommuneAdapter)
        }

        views.dob.transformIntoDatePicker(requireContext())
    }

    private fun handleViewEvents(event: ProfileViewEvents) {
        withState(viewModel) { state ->
            when {
                event is ProfileViewEvents.ApplyNewDistricts
                        && event.addressType == AddressType.PERM
                        && state.asyncDistricts is Success -> {
                    state.asyncDistricts.invoke()?.let {
                        updateDistricts(it, event.addressType)
                        if (event.clearCommune == true) {
                            updateCommunes(mutableListOf(), event.addressType)
                        }
                    }
                }

                event is ProfileViewEvents.ApplyNewDistricts
                        && event.addressType == AddressType.CUR
                        && state.asyncDistricts2 is Success -> {
                    state.asyncDistricts2.invoke()?.let {
                        updateDistricts(it, event.addressType)
                        if (event.clearCommune == true) {
                            updateCommunes(mutableListOf(), event.addressType)
                        }
                    }
                }

                event is ProfileViewEvents.ApplyNewCommunes
                        && event.addressType == AddressType.PERM
                        && state.asyncCommunes is Success -> {
                    state.asyncCommunes.invoke()?.let {
                        updateCommunes(it, event.addressType)
                    }
                }

                event is ProfileViewEvents.ApplyNewCommunes
                        && event.addressType == AddressType.CUR
                        && state.asyncCommunes2 is Success -> {
                    state.asyncCommunes2.invoke()?.let {
                        updateCommunes(it, event.addressType)
                    }
                }

                else -> Unit
            }
        }
    }



    private fun populateData(state: ProfileViewState) {

        if (state.asyncEthnics is Success && state.asyncOccupations is Success && state.asyncProvinces is Success) {
            state.asyncEthnics.invoke()?.let { updateEthnics(it) }
            state.asyncOccupations.invoke()?.let { updateOccupations(it) }
            state.asyncProvinces.invoke()?.let { updateProvinces(it) }
        }

        if (state.asyncPatient is Success) {
            if (!this::editingPatient.isInitialized) {
                state.asyncPatient.invoke()?.let {
                    editingPatient = it
                    populatePatientData()
                }
            }
        }

        if (state.asyncPatientInfo is Success) {
            if (!this::editingPatient.isInitialized) {
                state.asyncPatientInfo.invoke()?.let {
                    if (it.patientId != null) {
                        viewModel.handle(ProfileAction.QueryPatientData(it.patientId))
                    }
                }
            }
        }
    }

    private fun populatePatientData() {
        with(editingPatient) {
            views.fullname.setText(this.fullName)
            views.dob.setText(this.dob?.format())
            if (this.dob != null) {
                views.dob.transformIntoDatePicker(requireContext(), currentDate = this.dob, maxDate = Date())
            }
            views.gender.setText(this.gender?.toLocalizedString(requireContext()), false)

            this.pregnant?.let {
                views.chkPregnant.isChecked = true
            }
            views.chkPregnant.visible(this.gender == Gender.F)

            views.occupation.setText(this.occupation?.name, false)
            views.ethnic.setText(this.ethnicity?.name, false)
            views.nationalId.setText(this.nationalIdNumber)
            views.emailAddress.setText(this.email)
            views.phoneNumber.setText(this.phoneNumber)
            views.shi.setText(this.shiNumber)
            views.supporterName.setText(this.supporterName)
            views.supporterPhone.setText(this.supporterPhoneNumber)
            views.supporterAddress.setText(this.supporterAddress)

            val pos = serviceTypes.indexOfFirst { it.a == this.serviceType }
            views.serviceType.setText(
                if (pos >= 0) serviceTypes.get(pos).b else null,
                false
            )

            var permProvince: AdminUnit? = null
            var permDistrict: AdminUnit? = null
            this.permCommune?.let { mCommune ->
                mCommune.parent?.let { mDistrict ->
                    permDistrict = mDistrict

                    mDistrict.parent?.let { permProvince = it }
                }
            }

            permProvince?.id?.let {
                viewModel.handle(ProfileAction.ProvinceSelected(it, AddressType.PERM, false))
            }

            permDistrict?.id?.let {
                viewModel.handle(ProfileAction.DistrictSelected(it, AddressType.PERM))
            }

            views.permProvince.setText(permProvince?.name, false)
            views.permDistrict.setText(permDistrict?.name, false)
            views.permCommune.setText(this.permCommune?.name, false)
            views.permStreetAddress.setText(this.permStreetAddress)

            var curProvince: AdminUnit? = null
            var curDistrict: AdminUnit? = null
            this.curCommune?.let { mCommune ->
                mCommune.parent?.let { mDistrict ->
                    curDistrict = mDistrict

                    mDistrict.parent?.let { curProvince = it }
                }
            }

            curProvince?.id?.let {
                viewModel.handle(ProfileAction.ProvinceSelected(it, AddressType.CUR, false))
            }

            curDistrict?.id?.let {
                viewModel.handle(ProfileAction.DistrictSelected(it, AddressType.CUR))
            }

            views.curProvince.setText(curProvince?.name, false)
            views.curDistrict.setText(curDistrict?.name, false)
            views.curCommune.setText(this.curCommune?.name, false)
            views.curStreetAddress.setText(this.curStreetAddress)

            val sameAddress = this.isSameAddress()
            views.chkSameAddress.isChecked = sameAddress
        }
    }

    private fun updateGenders() {
        genders.clear()
        genders.addAll(
            listOf(
                CustomPair(Gender.M, getString(R.string.gender_male)),
                CustomPair(Gender.F, getString(R.string.gender_female))
            )
        )
    }

    private fun updateServiceTypes() {
        serviceTypes.clear()
        serviceTypes.addAll(
            listOf(
//                CustomPair(0, getString(R.string.service_type_shi)),
                CustomPair(1, getString(R.string.service_type_paid)),
                CustomPair(2, getString(R.string.service_type_free)),
//                CustomPair(3, getString(R.string.service_type_other))
            )
        )
    }

    private fun updateOccupations(page: Page<Occupation>) {
        occupations.clear()
        page.content?.let { occupations.addAll(it) }
    }

    private fun updateEthnics(page: Page<Ethnicity>) {
        ethnics.clear()
        page.content?.let { ethnics.addAll(it) }
    }

    private fun updateProvinces(list: List<AdminUnit>) {
        provinces.clear()
        list.let { provinces.addAll(it) }
    }

    private fun updateDistricts(list: List<AdminUnit>, addressType: AddressType) {
        when (addressType) {
            AddressType.PERM -> {
                permDistricts.clear()
                list.let { permDistricts.addAll(it) }
            }

            AddressType.CUR -> {
                curDistricts.clear()
                list.let { curDistricts.addAll(it) }
            }
        }
    }

    private fun updateCommunes(list: List<AdminUnit>, addressType: AddressType) {
        when (addressType) {
            AddressType.PERM -> {
                permCommunes.clear()
                selPermCommunes.clear()
                list.let {
                    permCommunes.addAll(it)
                    selPermCommunes.addAll(it) // for storing
                }
            }

            AddressType.CUR -> {
                curCommunes.clear()
                selCurCommunes.clear()
                list.let {
                    curCommunes.addAll(it)
                    selCurCommunes.addAll(it) // for storing
                }
            }
        }
    }





}
