package im.vector.app.ext.registration

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.dropbox.core.util.StringUtil
import com.example.demofragment.KeyboardUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.hideKeyboardDrop
import im.vector.app.core.extensions.onlyNumber
import im.vector.app.databinding.GlobitsRegSection02Binding
import im.vector.app.ext.custom.AppCustom.Companion.transformIntoDatePicker
import im.vector.app.ext.custom.ExposedDropdownMenu
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AddressType
import im.vector.app.ext.data.type.CustomPair
import im.vector.app.ext.data.type.Gender
import im.vector.app.ext.utils.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RegistrationSection0Fragment2 @Inject constructor() :
    RegistrationBaseFragment<GlobitsRegSection02Binding>() {

    // genders
    private lateinit var genderAdapter: ArrayAdapter<CustomPair<Gender>>
    private val genders = mutableListOf<CustomPair<Gender>>()

    // service types
    private lateinit var serviceTypeAdapter: ArrayAdapter<CustomPair<Int>>
    private val serviceTypes = mutableListOf<CustomPair<Int>>()

    // highRiskGroup types
    private lateinit var highRiskGroupTypeAdapter: ArrayAdapter<CustomPair<Int>>
    private val highRiskGroupTypes = mutableListOf<CustomPair<Int>>()

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
    private lateinit var checkKeyboardUtils: KeyboardUtils


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection02Binding {
        return GlobitsRegSection02Binding.inflate(inflater, container, false)
    }

    fun nextTab() {
        RegistrationSection0Fragment.viewPager.setCurrentItem(2, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()

        formatdate(views.dob, views.dobTil)
        views.nextButton.setOnClickListener { nextTab() }
        viewModel.observeViewEvents {
            handleViewEvents(it)
        }

    }

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)
        populateData(state)
    }

    override fun resetViewModel() {
    }

    override fun updateData() {
        if (!this::editingPatient.isInitialized) {
            return
        }

        with(editingPatient) {
            this.fullName = views.fullname.toStringAlt()

            try {
                this.dob = views.dob.parseDate()
                if (this.dob != null && this.dob!! > Date())
                    this.dob = null
            } catch (e: Exception) {
                this.dob = null
            }
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

            pos =
                highRiskGroupTypes.indexOfFirst { it.b == views.highRiskGroupType.text.toString() }
            this.highRiskGroup = if (pos >= 0) highRiskGroupTypeAdapter.getItem(pos)?.a else null

            // perm address
            pos = selPermCommunes.indexOfFirst { it.name == views.permCommune.text.toString() }
            this.permCommune = if (pos >= 0) permCommuneAdapter.getItem(pos) else null
            this.permStreetAddress = views.permStreetAddress.toStringAlt()

            // cur address
            pos = selCurCommunes.indexOfFirst { it.name == views.curCommune.text.toString() }
            this.curCommune = if (pos >= 0) curCommuneAdapter.getItem(pos) else null
            this.curStreetAddress = views.curStreetAddress.toStringAlt()

            if (views.chkSameAddress.isChecked) {
                this.curStreetAddress = views.permStreetAddress.toStringAlt()
            }
            if (RegistrationSection0Fragment.checkpatient)
                validateInputs()
        }
    }

    private fun validateInputs() {
        validateField(views.fullname, true, -1, views.fullnameTil)
        validateField(views.emailAddress, true, -1, views.emailAddressTil)
        validateField(views.dob, true, -1, views.dobTil)
        validateFieldDob(views.dob, views.dobTil)
        validateField(views.nationalId, true, -1, views.nationalIdTil)
        validateFieldPhoneAndNational(views.nationalId, views.nationalIdTil)
        validateField(views.phoneNumber, true, 10, views.phoneNumberTil)
        validateFieldPhoneAndNational(views.phoneNumber, views.phoneNumberTil)
        validateMenu(views.occupation, true, views.occupationTil)
        validateMenu(views.gender, true, views.genderTil)
        validateMenu(views.ethnic, true, views.ethnicTil)
        validateMenu(views.serviceType, true, views.serviceTypeTil)
        validateMenu(views.permCommune, true, views.permCommuneTil)
        validateMenu(views.curCommune, true, views.curCommuneTil)
        validateMenu(views.permDistrict, true, views.permDistrictTil)
        validateMenu(views.permProvince, true, views.permProvinceTil)
        validateMenu(views.highRiskGroupType, true, views.highRiskGroupTypeTil)

        if (!isEmailValid(views.emailAddress.toStringAlt().trim())) {
            getString(R.string.validate_email).also { views.emailAddressTil.error = it }
        }
    }

    private fun validateFieldPhoneAndNational(
        textField: TextInputEditText,
        text: TextInputLayout
    ) {
        if (!textField.toStringAlt().isNullOrEmptyAlt() && !textField.onlyNumber()) {
            getString(R.string.msg_field_invaild).also {
                text.error = it
            }
        }
    }

    private fun validateFieldDob(
        textField: TextInputEditText,
        text: TextInputLayout
    ) {
        if (!textField.toStringAlt().isNullOrEmptyAlt()) {
            var dob: Date? = null
            try {
                dob = textField.parseDate()
            } catch (e: Exception) {
                textField.requestFocus()
                text.error = getString(R.string.date_invalid)
            }
            if (dob == null) {
                textField.requestFocus()
                text.error = getString(R.string.date_invalid)
            }
            if (dob != null && dob > Date()) {
                textField.requestFocus()
                text.error = getString(R.string.date_than_current)
            }
        }
    }

    private fun validateField(
        textField: TextInputEditText,
        required: Boolean,
        min: Int,
        text: TextInputLayout
    ) {
        if (required && textField.toStringAlt().isNullOrEmptyAlt()) {
            getString(R.string.msg_required_field).also {
                text.error = it
            }
            textField.requestFocus()
        }
        if (min != -1 && (!textField.toStringAlt()
                .isNullOrEmptyAlt() && textField.toStringAlt().length < min)
        ) {
            getString(R.string.msg_validate_min_field).also {
                text.error = it

            }
            textField.requestFocus()
        }

        textField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (textField == views.dob) {
                    val result = views.dob.text.toString()
                    // Use the Kotlin extension in the fragment-ktx artifact
                    setFragmentResult("requestKey", bundleOf("bundleKey" to result))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text.error = null
            }
        })
    }

    private fun validateMenu(ex: ExposedDropdownMenu, required: Boolean, text: TextInputLayout) {
        if (required && ex.text.isNullOrEmpty()) {
            getString(R.string.msg_required_field).also {
                text.error = it
            }
            ex.requestFocus()
        }

        ex.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text.error = null
            }

        })

    }


    private fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }


    override fun onPause() {
        super.onPause()
        updateData()
    }

    ///////////////////////////// PRIVATE ///////////////////////////////////

    private fun initiateUI() {


        // query common data
        viewModel.handle(RegistrationActions.QueryCommonData4ProfileEdit)

        // gender
        genderAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, genders)
        views.gender.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(genderAdapter)
        }

        updateGenders()

        views.chkPregnant.visible(false)
        views.gender.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            views.chkPregnant.visible(position == 1)
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


        // service types
        serviceTypeAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, serviceTypes)
        views.serviceType.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(serviceTypeAdapter)
        }

        updateServiceTypes()


        highRiskGroupTypeAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout._globits_simple_select_list_item,
                highRiskGroupTypes
            )
        views.highRiskGroupType.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(highRiskGroupTypeAdapter)
        }

        updatehighRiskGroupTypes()


        // occupations
        occupationAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, occupations)
        views.occupation.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(occupationAdapter)
        }

        // ethnics
        ethnicAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, ethnics)
        views.ethnic.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(ethnicAdapter)
        }

        // address
//        views.chkSameAddress.isChecked = true
//        views.curAddressTil.visible(false)
        views.chkSameAddress.setOnCheckedChangeListener { _, checked ->
            views.curAddressTil.visible(!checked)
        }

        // provinces
        permProvinceAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, provinces)
        views.permProvince.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(permProvinceAdapter)
        }

        curProvinceAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, provinces)
        views.curProvince.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(curProvinceAdapter)

        }

        views.permProvince.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                val province = provinces.getOrNull(position)
                if (province?.id != null) {
                    viewModel.handle(
                        RegistrationActions.ProvinceSelected(
                            province.id,
                            AddressType.PERM
                        )
                    )

                    // clear selected districts and communes
                    views.permDistrict.setText(null, false)
                    views.permCommune.setText(null, false)

                    if (views.chkSameAddress.isChecked) {
                        views.curProvince.setText(province.name, false)
                        viewModel.handle(
                            RegistrationActions.ProvinceSelected(
                                province.id,
                                AddressType.CUR
                            )
                        )

                        views.curDistrict.setText(null, false)
                        views.curCommune.setText(null, false)
                        views.curStreetAddress.text = null
                    }
                }
            }
        views.curProvince.onItemClickListener =

            AdapterView.OnItemClickListener { _, _, position, _ ->

                val province = provinces.getOrNull(position)
                if (province?.id != null) {
                    viewModel.handle(
                        RegistrationActions.ProvinceSelected(
                            province.id,
                            AddressType.CUR
                        )
                    )

                    // clear selected districts and communes
                    views.curDistrict.setText(null, false)
                    views.curCommune.setText(null, false)
                }


            }


        // districts
        permDistrictAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, permDistricts)
        views.permDistrict.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(permDistrictAdapter)
        }

        curDistrictAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, curDistricts)
        views.curDistrict.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(curDistrictAdapter)
        }

        views.permDistrict.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val district = permDistricts.getOrNull(position)
                if (district?.id != null) {
                    viewModel.handle(
                        RegistrationActions.DistrictSelected(
                            district.id,
                            AddressType.PERM
                        )
                    )

                    // clear selected communes
                    views.permCommune.setText(null, false)

                    if (views.chkSameAddress.isChecked) {
                        views.curDistrict.setText(district.name, false)
                        viewModel.handle(
                            RegistrationActions.DistrictSelected(
                                district.id,
                                AddressType.CUR
                            )
                        )

                        views.curCommune.setText(null, false)
                        views.curStreetAddress.text = null
                    }
                }

            }

        views.curDistrict.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                val district = curDistricts.getOrNull(position)
                if (district?.id != null) {
                    viewModel.handle(
                        RegistrationActions.DistrictSelected(
                            district.id,
                            AddressType.CUR
                        )
                    )

                    // clear selected communes
                    views.curCommune.setText(null, false)
                }

            }

        // communes
        permCommuneAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, permCommunes)
        views.permCommune.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(permCommuneAdapter)
        }

        views.permCommune.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val commune = permCommunes.getOrNull(position)
                if (commune?.id != null && views.chkSameAddress.isChecked) {
                    views.curCommune.setText(commune.name, false)
                }
            }

        curCommuneAdapter =
            ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, curCommunes)
        views.curCommune.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(curCommuneAdapter)
        }

        views.dob.transformIntoDatePicker(requireContext(), maxDate = Date())

        views.dob.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val result = views.dob.text.toString()
                // Use the Kotlin extension in the fragment-ktx artifact
                setFragmentResult("requestKey", bundleOf("bundleKey" to result))

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

    }


    private fun updatehighRiskGroupTypes() {
        highRiskGroupTypes.clear()
        highRiskGroupTypes.addAll(
            listOf(
                CustomPair(1, getString(R.string.highrisk_type_infectioussex)),
                CustomPair(2, getString(R.string.highrisk_type_transgenmale)),
                CustomPair(3, getString(R.string.highrisk_type_transgenfemale)),
                CustomPair(4, getString(R.string.highrisk_type_different)),
                CustomPair(5, getString(R.string.highrisk_type_msm)),
                CustomPair(6, getString(R.string.highrisk_type_prostitutes)),
                CustomPair(7, getString(R.string.highrisk_type_TCMT))
            )
        )
    }

    private fun handleViewEvents(event: RegistrationViewEvents) {
        withState(viewModel) { state ->
            when {
                event is RegistrationViewEvents.ApplyNewDistricts
                        && event.addressType == AddressType.PERM
                        && state.asyncDistricts is Success -> {
                    state.asyncDistricts.invoke()?.let {
                        updateDistricts(it, event.addressType)
                        if (event.clearCommune == true) {
                            updateCommunes(mutableListOf(), event.addressType)
                        }
                    }
                }

                event is RegistrationViewEvents.ApplyNewDistricts
                        && event.addressType == AddressType.CUR
                        && state.asyncDistricts2 is Success -> {
                    state.asyncDistricts2.invoke()?.let {
                        updateDistricts(it, event.addressType)
                        if (event.clearCommune == true) {
                            updateCommunes(mutableListOf(), event.addressType)
                        }
                    }
                }

                event is RegistrationViewEvents.ApplyNewCommunes
                        && event.addressType == AddressType.PERM
                        && state.asyncCommunes is Success -> {
                    state.asyncCommunes.invoke()?.let {
                        updateCommunes(it, event.addressType)
                    }
                }

                event is RegistrationViewEvents.ApplyNewCommunes
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

    private fun populateData(state: RegistrationViewState) {
        when {
            state.asyncEthnics is Success && state.asyncOccupations is Success && state.asyncProvinces is Success -> {
                state.asyncEthnics.invoke()?.let { updateEthnics(it) }
                state.asyncOccupations.invoke()?.let { updateOccupations(it) }
                state.asyncProvinces.invoke()?.let { updateProvinces(it) }
            }

            state.asyncPatient is Success -> {
                if (!this::editingPatient.isInitialized) {
                    state.asyncPatient.invoke()?.let {
                        editingPatient = it
                        populatePatientData()
                    }
                }
            }

            else -> Unit
        }
    }

    private fun populatePatientData() {
        with(editingPatient) {
            views.fullname.setText(this.fullName)
            views.dob.setText(this.dob?.format())
            if (this.dob != null) {
                views.dob.transformIntoDatePicker(
                    requireContext(),
                    currentDate = this.dob,
                    maxDate = Date()
                )


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

            val pos = serviceTypes.indexOfFirst { it.a == this.serviceType }
            views.serviceType.setText(
                if (pos >= 0) serviceTypes.get(pos).b else null,
                false
            )

            val posHighRisk = highRiskGroupTypes.indexOfFirst { it.a == this.highRiskGroup }
            views.highRiskGroupType.setText(
                if (posHighRisk >= 0) highRiskGroupTypes.get(posHighRisk).b else null,
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
                viewModel.handle(RegistrationActions.ProvinceSelected(it, AddressType.PERM, false))
            }

            permDistrict?.id?.let {
                viewModel.handle(RegistrationActions.DistrictSelected(it, AddressType.PERM))
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
                viewModel.handle(RegistrationActions.ProvinceSelected(it, AddressType.CUR, false))
            }

            curDistrict?.id?.let {
                viewModel.handle(RegistrationActions.DistrictSelected(it, AddressType.CUR))
            }

            views.curProvince.setText(curProvince?.name, false)
            views.curDistrict.setText(curDistrict?.name, false)
            views.curCommune.setText(this.curCommune?.name, false)
            views.curStreetAddress.setText(this.curStreetAddress)

            val sameAddress = this.isSameAddress()
            views.chkSameAddress.isChecked = sameAddress
            views.curAddressTil.visible(!sameAddress)
            if (profileStatus!! > 2) {
                views.fullnameTil.isEnabled = false
                views.dobTil.isEnabled = false
                views.genderTil.isEnabled = false
                views.chkPregnant.isEnabled = false
                views.occupationTil.isEnabled = false
                views.ethnicTil.isEnabled = false
                views.nationalIdTil.isEnabled = false
                views.emailAddressTil.isEnabled = false
                views.phoneNumberTil.isEnabled = false
                views.shiTil.isEnabled = false
                views.serviceTypeTil.isEnabled = false
                views.highRiskGroupTypeTil.isEnabled = false
                views.curProvinceTil.isEnabled = false

                views.curDistrictTil.isEnabled = false
                views.curCommuneTil.isEnabled = false
                views.curStreetAddressTil.isEnabled = false
                views.chkSameAddress.isEnabled = false
                views.permProvinceTil.isEnabled = false
                views.permDistrictTil.isEnabled = false
                views.permStreetAddressTil.isEnabled = false
                views.permCommuneTil.isEnabled = false


            }

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
        permProvinceAdapter.notifyDataSetChanged()
        curProvinceAdapter.notifyDataSetChanged()

    }

    private fun updateDistricts(list: List<AdminUnit>, addressType: AddressType) {
        when (addressType) {
            AddressType.PERM -> {

                permDistricts.clear()
                list.let { permDistricts.addAll(it) }
                permDistrictAdapter.notifyDataSetChanged()

            }

            AddressType.CUR -> {

                curDistricts.clear()
                list.let { curDistricts.addAll(it) }
                curDistrictAdapter.notifyDataSetChanged()

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
                permCommuneAdapter.notifyDataSetChanged()

            }

            AddressType.CUR -> {

                curCommunes.clear()
                selCurCommunes.clear()
                list.let {
                    curCommunes.addAll(it)
                    selCurCommunes.addAll(it) // for storing
                }
                curCommuneAdapter.notifyDataSetChanged()

            }
        }
    }



}
