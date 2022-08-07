package im.vector.app.ext.registration

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.google.android.material.datepicker.MaterialDatePicker
import im.vector.app.R
import im.vector.app.core.extensions.getAllChildFragments
import im.vector.app.core.extensions.hideKeyboardDrop
import im.vector.app.databinding.GlobitsRegSection4Binding
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.CustomPair
import im.vector.app.ext.registration.custom.RegistrationDialog
import im.vector.app.ext.utils.clickWithThrottle
import im.vector.app.ext.utils.snackbar
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class RegistrationSection4Fragment @Inject constructor(
    private val session: Session
) : RegistrationBaseFragment<GlobitsRegSection4Binding>() {

    override var toolbarTitleRes: Int? = R.string.make_appointment_title

    // appointment place
    private lateinit var appointmentPlaceAdapter: ArrayAdapter<CustomPair<Int>>
    private val appointmentPlaces = mutableListOf<CustomPair<Int>>()

    // encounterSlot
    private lateinit var encounterSlotAdapter: ArrayAdapter<EncounterSlot>
    private val encounterSlots = mutableListOf<EncounterSlot>()

    // edit appointment
    private lateinit var editingAppointment: Appointment
    private lateinit var patientInfo: PatientInfo

    private lateinit var currentDate: Date
    var isPulatedata = false

//    private lateinit var labTestOrder: LabTestOrder


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection4Binding {
        return GlobitsRegSection4Binding.inflate(inflater, container, false)
    }

    companion object {
        const val TAG = "_REGISTRATION_MAKE_APPOINTMENT"
        var count = false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(RegistrationActions.QueryPatientInfoData)
        initUI()
        viewModel.observeViewEvents {
            handleViewEvents(it)
        }

        viewModel.handle(RegistrationActions.QueryCommonDataLabTestEdit)

    }

    private fun handleViewEvents(event: RegistrationViewEvents) {
        when (event) {
            is RegistrationViewEvents.SaveAppointmentComplete -> {
                withState(viewModel) {
                    when (it.asyncEditingAppointment) {
                        is Success -> {
                            requireActivity().snackbar(getString(R.string.data_successfully_saved))
                        }
                        is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
                        else -> Unit
                    }
                }
            }
            is RegistrationViewEvents.QueryAppointment -> {
                withState(viewModel) {
                    when (it.asyncAppointment) {
                        is Success -> {
                            it.asyncAppointment.invoke()?.let {
                                editingAppointment = it
                                populateAppointmentData()
                                isPulatedata = true

                            }
                        }
                        is Fail -> requireActivity().snackbar(getString(R.string.data_error))
                        else -> Unit
                    }
                }
            }
            is RegistrationViewEvents.GetEncounterSlot -> {
                withState(viewModel) {
                    if (it.asyncEncounterSlot is Success) {
                        it.asyncEncounterSlot.invoke()?.let {
                            updateEncounterSlot(it)
                            if (isPulatedata) {
                                populateAppointmentData()
                                isPulatedata=false
                            }
                        }

                    }
                }
            }
            else -> Unit
        }
    }

    private fun setUpCurrentDate() {
        currentDate = Date()
        if (!this::patientInfo.isInitialized) {
            return
        }
        if (patientInfo.encounter?.encounterDate != null && patientInfo.encounter?.appointment == null) {
            currentDate = patientInfo.encounter?.encounterDate!!
        } else if (patientInfo.encounter?.appointment?.start != null) {
            currentDate = patientInfo.encounter?.appointment?.start!!
        }
        val cal = Calendar.getInstance()
        cal.time = currentDate
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        views.dpAppointment.updateDate(year, month, day)
    }

    private fun initUI() {
        //setupDatePicker
        val calendar = Calendar.getInstance()
        views.dpAppointment.minDate = Date().time
        calendar.timeInMillis = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            views.dpAppointment.setOnDateChangedListener { _, year, month, day ->
                calendar.set(year, month, day)
                currentDate = calendar.time
                viewModel.handle(RegistrationActions.GenerateEncounterSlot(calendar.time))
            }
        } else {
            views.dpAppointment.init(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ) { _, year, month, day ->
                calendar.set(year, month, day)
                currentDate = calendar.time
                viewModel.handle(RegistrationActions.GenerateEncounterSlot(calendar.time))
            }
        }

        // encounterSlot
        encounterSlotAdapter = ArrayAdapter(
            requireContext(),
            R.layout._globits_simple_list_item2,
            encounterSlots
        )
        views.encounterSlot.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(encounterSlotAdapter)
        }

        // appointmentPlace
        appointmentPlaceAdapter = ArrayAdapter(
            requireContext(),
            R.layout._globits_simple_list_item2,
            appointmentPlaces
        )
        views.appointmentPlace.apply {
            hideKeyboardDrop(requireActivity())
            setAdapter(appointmentPlaceAdapter)
        }

        updateAppointmentPlace()

        views.btnAppointment.clickWithThrottle {
            updateData()
            handleSubmit()
        }

    }

    private fun handleSubmit() {
        count = true
        editingAppointment.let {
            if (it.slot == null) {
                requireActivity().snackbar(getString(R.string.validate_encounter_slot))
                return
            }
            if (it.place == null) {
                requireActivity().snackbar(getString(R.string.validate_encounter_place))
                return
            }
            Timber.e("appointment --> %s", it.toString())
            viewModel.handle(RegistrationActions.SaveEditingAppointment(it))
            viewModel.handle(RegistrationActions.QueryPatientInfoData)

        }
    }

    private fun updateAppointmentPlace() {
        appointmentPlaces.clear()
        appointmentPlaces.addAll(
            listOf(
                CustomPair(1, getString(R.string.appointment_place_tele)),
                CustomPair(2, getString(R.string.appointment_place_clinic)),
            )
        )
    }

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)
        if (state.asyncPatientInfo is Success) {
            if (!this::patientInfo.isInitialized) {
                state.asyncPatientInfo.invoke()?.let {
                    patientInfo = it
                    setUpCurrentDate()
                    viewModel.handle(RegistrationActions.GenerateEncounterSlot(currentDate))
                    if (it.encounter?.appointment?.id != null) {
                        viewModel.handle(RegistrationActions.QueryAppointmentById(it.encounter.appointment.id))
                    }
                }
            }
        }


        if (state.asyncEditingAppointment is Success) {
            if (count && views.encounterSlot.text.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "đặt lịch khám thành công",
                    Toast.LENGTH_SHORT
                ).show()
                RegistrationDialog.createDialog(
                    requireContext(), "THÔNG BÁO",
                    "Đặt lịch khám thành công", this
                )
            }
            count = false
        } else if (state.asyncEditingAppointment is Fail && count) {
            Toast.makeText(
                requireContext(),
                "Có lỗi xảy ra khi đặt lịch khám",
                Toast.LENGTH_SHORT
            ).show()
            count = false
        }


        if (state.asyncLabTestOrder is Success) {
            state.asyncLabTestOrder.invoke().let {
                if (it != null) {
                    checkResultLabTest(it)
                }
            }
        }
    }

    fun isNumber(s: String): Boolean {
        return when (s.toIntOrNull()) {
            null -> false
            else -> true
        }
    }

    private fun checkResultLabTest(it: LabTestOrder) {
        var checkresult = true

        for (labitem in it.items!!) {
            if (labitem.stringResult?.let { it1 -> isNumber(it1) } == true) {
                if (labitem.stringResult.toLong() < 60L)
                    checkresult = false
            } else {
                if (labitem.stringResult == "Có phản ứng")
                    checkresult = false
            }
        }

        if (!checkresult) {
            views.encounterLayout.visibility = View.INVISIBLE
            views.txtEncouterNotifi.visibility = View.VISIBLE
        } else {
            views.txtEncouterNotifi.visibility = View.GONE

        }
    }

    private fun populateAppointmentData() {

        with(editingAppointment) {

            var pos = appointmentPlaces.indexOfFirst { it.a == this.place }
            views.appointmentPlace.setText(
                if (pos >= 0) appointmentPlaces[pos].b else null,
                false
            )

            if (this.slot != null) {
                pos = encounterSlots.indexOfFirst { it.id.toString() == this.slot?.id.toString() }
                views.encounterSlot.setText(
                    if (pos >= 0) encounterSlots[pos].name else null,
                    false
                )
            }

            if (editingAppointment.id != null) {
                views.btnAppointment.text = getString(R.string.change_appointment)
            }
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun updateEncounterSlot(list: List<EncounterSlot>) {
        if (list.isNotEmpty()) {
            val customList = mutableListOf<EncounterSlot>()
            val formatter = SimpleDateFormat("HH:mm")
            var date = Date()
            var now = formatter.format(date)

            list.forEach() {

                val start: String = formatter.format(it.start)
                val end: String = formatter.format(it.end)

                var slot: EncounterSlot = it;
                slot.name = String.format("%s - %s", start, end)

                if (
                    start.replace(':', '.').toFloat() >= now.replace(':', '.').toFloat()
//                   || currentDate.after(Date())
                    || getDateFromDatePicker(views.dpAppointment)!!.after(Date())
                )
                    customList.add(slot)
            }

            encounterSlots.clear()
            list.let { encounterSlots.addAll(customList) }
            encounterSlotAdapter.notifyDataSetChanged();
        }
    }

    override fun resetViewModel() {
    }

    override fun updateData() {
        if (!this::editingAppointment.isInitialized) {
            editingAppointment = Appointment()
        }
        with(editingAppointment) {
            var pos = encounterSlots.indexOfFirst { it.name == views.encounterSlot.text.toString() }
            this.slot = if (pos >= 0) encounterSlots[pos] else null
            pos = appointmentPlaces.indexOfFirst { it.b == views.appointmentPlace.text.toString() }
            this.place = if (pos >= 0) appointmentPlaces[pos].a else null
            this.start = currentDate
            this.end = currentDate
            var patient = Patient()
            patient.id = patientInfo.patientId
            this.patient = patient
            this.encounterId = patientInfo.encounter?.id
            this.patientId = patientInfo.patientId
        }
    }


    fun getDateFromDatePicker(datePicker: DatePicker): Date? {
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return calendar.time
    }

    /////////////////////////// PRIVATE //////////////////////////////
}
