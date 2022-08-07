package im.vector.app.ext.encounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.databinding.GlobitsFragmentAppointmentBinding
import im.vector.app.ext.data.model.Encounter
import im.vector.app.ext.data.model.TreatmentPeriod
import im.vector.app.ext.encounter.list.EncounterAdapter
import javax.inject.Inject

class EncounterFragment @Inject constructor() : EncounterBaseFragment<GlobitsFragmentAppointmentBinding>() {

    private lateinit var patientId: String

    private lateinit var treatmentPeriodAdapter: ArrayAdapter<TreatmentPeriod>
    private val treatmentPeriodList = mutableListOf<TreatmentPeriod>()

    private val encounterList = mutableListOf<Encounter>()

    override fun onResume() {
        super.onResume()
        //viewModel.handle(EncounterActions.QueryPatientInfoData)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsFragmentAppointmentBinding {
        return GlobitsFragmentAppointmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        viewModel.handle(EncounterActions.QueryPatientInfoData)
    }

    private fun initUI() {

        treatmentPeriodAdapter = ArrayAdapter(requireContext(), R.layout._globits_simple_select_list_item, treatmentPeriodList)

    }

    private fun setupEncounterList(treatmentPeriod: TreatmentPeriod) {
        encounterList.clear()
        treatmentPeriod.encounters?.let { encounterList.addAll(it) }
        views.recyclerView.layoutManager = LinearLayoutManager(context)
        views.recyclerView.setHasFixedSize(true)
        views.recyclerView.adapter =
            EncounterAdapter(encounterList as ArrayList<Encounter>, viewModel)
    }

    override fun updateWithState(state: EncounterViewState) {
        if (state.asyncPatientInfo is Success) {
            if (!this::patientId.isInitialized) {
                state.asyncPatientInfo.invoke().let {
                    patientId = it.patientId!!
                    viewModel.handle(EncounterActions.QueryTreatmentPeriodByPatientId(patientId))
                }
            }
        }
        if (state.asyncListTreatmentPeriod is Success) {
            state.asyncListTreatmentPeriod.invoke().let {
                if (it.isNotEmpty()) {
                    for (i in it.indices) {
                        it[i].name = "Đợt ${it.size - i}"
                    }
                    setupEncounterList(it[0])
                    treatmentPeriodList.clear()
                    treatmentPeriodList.addAll(it)

                }
            }
        }

    }

    override fun resetViewModel() {

    }

    override fun updateData() {

    }
}
