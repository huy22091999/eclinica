package im.vector.app.ext.registration

import android.os.Bundle
import android.view.*
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.databinding.GlobitsServiceRatingBinding
import im.vector.app.ext.data.model.*
import im.vector.app.ext.utils.snackbar
import javax.inject.Inject

class ServiceRatingFragment @Inject constructor() : RegistrationBaseFragment<GlobitsServiceRatingBinding>() {

    override var toolbarTitleRes: Int? = R.string.service_rating

    private lateinit var encounterId: String

    private lateinit var patientId: String

    private lateinit var serviceEvaluationCardByEncounter: ServiceEvaluationCardByEncounter

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsServiceRatingBinding {
        return GlobitsServiceRatingBinding.inflate(inflater, container, false)
    }

    companion object {
        const val TAG = "_ENCOUNTER_SERVICE_RATING"
    }

    private fun handleViewEvents(event: RegistrationViewEvents) {
        if (event is RegistrationViewEvents.SaveServiceRatingComplete) {
            withState(viewModel) {
                when (it.asyncEditingServiceRating) {
                    is Success -> {
                        requireActivity().snackbar(getString(R.string.data_successfully_saved))
                        serviceEvaluationCardByEncounter = it.asyncEditingServiceRating.invoke()!!
                    }
                    is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.observeViewEvents {
            handleViewEvents(it)
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

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)

        if (state.asyncPatientInfo is Success) {
            if (!this::encounterId.isInitialized) {
                state.asyncPatientInfo.invoke()?.let {
                    encounterId = it.encounter?.id.toString()
                    patientId = it.patientId!!
                    viewModel.handle(RegistrationActions.QueryServiceRating(patientId, encounterId))
                }
            }
        }

        if (state.asyncServiceRating is Success) {
            state.asyncServiceRating.invoke()?.let {
                serviceEvaluationCardByEncounter = it
                populateData()
            }
        }
    }

    private fun saveData() {
        // update data from views
        if (!this::serviceEvaluationCardByEncounter.isInitialized) {
            serviceEvaluationCardByEncounter = ServiceEvaluationCardByEncounter()
        }
        with(serviceEvaluationCardByEncounter) {
            this.question1 = views.serviceRatingQ1.rating.toInt()
            this.question2 = views.serviceRatingQ2.rating.toInt()
            this.question3 = views.serviceRatingQ3.rating.toInt()
            this.question4 = views.serviceRatingQ4.rating.toInt()
            this.question5 = views.serviceRatingQ5.rating.toInt()
            this.question6 = views.serviceRatingQ6.rating.toInt()
            this.question7 = views.serviceRatingQ7.rating.toInt()
            this.question8 = views.serviceRatingQ8.rating.toInt()
            this.question9 = views.serviceRatingQ9.rating.toInt()
            this.question10 = views.serviceRatingQ10.rating.toInt()
            this.question11 = views.serviceRatingQ11.rating.toInt()

            if (this.patient == null) {
                val patient = Patient()
                patient.id = patientId
                this.patient = patient
            }
            if (this.encounter == null) {
                val encounter = Encounter()
                encounter.id = encounterId
                this.encounter = encounter
            }
        }

        viewModel.handle(RegistrationActions.SaveServiceRating(serviceEvaluationCardByEncounter))

    }

    private fun populateData() {
        if (!this::serviceEvaluationCardByEncounter.isInitialized) {
            return
        }

        with(serviceEvaluationCardByEncounter) {

            this.question1?.let {
                views.serviceRatingQ1.rating = it.toFloat()
            }
            this.question2?.let {
                views.serviceRatingQ2.rating = it.toFloat()
            }
            this.question3?.let {
                views.serviceRatingQ3.rating = it.toFloat()
            }
            this.question4?.let {
                views.serviceRatingQ4.rating = it.toFloat()
            }
            this.question5?.let {
                views.serviceRatingQ5.rating = it.toFloat()
            }
            this.question6?.let {
                views.serviceRatingQ6.rating = it.toFloat()
            }
            this.question7?.let {
                views.serviceRatingQ7.rating = it.toFloat()
            }
            this.question8?.let {
                views.serviceRatingQ8.rating = it.toFloat()
            }
            this.question9?.let {
                views.serviceRatingQ9.rating = it.toFloat()
            }
            this.question10?.let {
                views.serviceRatingQ10.rating = it.toFloat()
            }
            this.question11?.let {
                views.serviceRatingQ11.rating = it.toFloat()
            }

        }
    }

    override fun resetViewModel() {
    }

    override fun updateData() {


    }

    /////////////////////////// PRIVATE //////////////////////////////
}
