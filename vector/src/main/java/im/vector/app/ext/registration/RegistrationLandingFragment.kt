package im.vector.app.ext.registration

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.databinding.GlobitsFragmentRegistrationBinding
import im.vector.app.ext.conference.ConferenceActivity
import im.vector.app.ext.data.model.ClientStepMessage
import im.vector.app.ext.data.model.Filedto
import im.vector.app.ext.utils.clickWithThrottle
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import javax.inject.Inject

class RegistrationLandingFragment @Inject constructor(
    private val session: Session,
) : RegistrationBaseFragment<GlobitsFragmentRegistrationBinding>() {

    private var queryPatientHandled = false
    private var queryPatientPrepScreeningHandled = false
    override var toolbarTitleRes: Int? = R.string.registration

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsFragmentRegistrationBinding {
        return GlobitsFragmentRegistrationBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.handle(RegistrationActions.QueryPatientInfoData)
        viewModel.handle(RegistrationActions.QueryStepMessage)
    }

    override fun updateWithState(state: RegistrationViewState) {
        setupClickListeners(state)
        setupStep(state)
        setupReExamination(state)
    }

    private fun setupReExamination(state: RegistrationViewState) {
        //tái khám ẩn 2 bước đầu
        if (state.asyncPatientInfo is Success && state.asyncStepMessage is Success) {

            state.asyncPatientInfo.invoke()?.let {
                viewModel.handle(
                    RegistrationActions.searchFileDto(
                        Filedto(
                            1,
                            20,
                            it.patientId,
                            1
                        )
                    )

                )
            }


            val patientInfo = state.asyncPatientInfo.invoke() ?: return
            val stepMessage = state.asyncStepMessage.invoke() ?: return
            if (patientInfo.encounter?.encounterType != null && patientInfo.encounter.encounterType == 1
                && stepMessage.step != -1
            ) {
                views.btnProfileInformation.visibility = View.GONE
                views.btnPrepRegistration.visibility = View.GONE
                views.icon1.visibility = View.GONE
                views.icon2.visibility = View.GONE
            }
        }
    }

    private fun setupStep(state: RegistrationViewState) {
        if (state.asyncStepMessage is Success) {

            val asyncStepMessage: ClientStepMessage = state.asyncStepMessage.invoke() ?: return
            if (asyncStepMessage.step == null || !(asyncStepMessage.step >= -1 && asyncStepMessage.step <= 2)) {
                views.btnPrepRegistration.isEnabled = false
                toggleButton(R.id.btn_prep_registration_til, R.id.image2, false)
                toggleImage(1, false)
                if (asyncStepMessage.step !=  null && asyncStepMessage.step > 2 ) {
                    toggleImage(1, true)
                }
            }
            if (asyncStepMessage.step != 2) {
                views.btnSelectLabtests.isEnabled = false
                toggleButton(R.id.btn_select_labtests_til, R.id.image3, false)
                toggleImage(2, false)
            }
            if( asyncStepMessage.step !=  null && asyncStepMessage.step >= 2){
                toggleImage(2, true)
            }
            if (!(asyncStepMessage.step == 3 || asyncStepMessage.step == 4 || asyncStepMessage.step == 5)) {
                views.btnUpdateLabresults.isEnabled = false
                toggleButton(R.id.btn_update_labresults_til, R.id.image4, false)
                toggleImage(3, false)
            }
            if(asyncStepMessage.step !=  null &&asyncStepMessage.step >= 3)
            {
                toggleImage(3, true)
            }
            if (!(asyncStepMessage.step == 4 || asyncStepMessage.step == 5)) {
                views.btnMakeAppointment.isEnabled = false
                toggleButton(R.id.btn_make_appointment_til, R.id.image5, false)
                toggleImage(4, false)
            }
            if(asyncStepMessage.step !=  null &&asyncStepMessage.step >= 4)
            {
                toggleImage(4, true)
            }

        }
    }

    override fun resetViewModel() {
    }

    override fun updateData() {
    }

    ////////////// PRIVATE ////////////////////

    private fun setupClickListeners(state: RegistrationViewState) {

        initButtons()

        if (state.asyncPatientInfo is Success) {

            val patientInfo = state.asyncPatientInfo.invoke() ?: return

            Timber.e("---------------------->>>>> >>>>>")
            Timber.e(
                "Treatment status = %s, info updated = %s",
                patientInfo.treatmentStatus,
                patientInfo.infoUpdated.toString()
            )
            Timber.e("---------------------->>>>> >>>>>")

            if (patientInfo.infoUpdated == true && patientInfo.patientId != null) {
                toggleButton(R.id.btn_prep_registration_til, R.id.image2, true)
                toggleImage(1, true)
            }

            // get the patient record
            if (!queryPatientHandled && patientInfo.patientId != null) {
                queryPatientHandled = true
                viewModel.handle(RegistrationActions.QueryPatientData(patientInfo.patientId))

                // query the latest screening result
                if (patientInfo.lastPatientPrepScreeningId == null) {
                    viewModel.handle(RegistrationActions.QueryPeriodPrepScreen(patientInfo.lastPatientPrepScreeningId))
                }
            }
            // query the latest screening result
            if (!queryPatientPrepScreeningHandled && patientInfo.lastPatientPrepScreeningId != null) {
                queryPatientPrepScreeningHandled = true
                viewModel.handle(RegistrationActions.QueryPeriodPrepScreen(patientInfo.lastPatientPrepScreeningId))
            }
            toggleButton(R.id.btn_profile_information_til, R.id.image1, true)
//            toggleButton(R.id.btn_prep_registration, R.id.prep_reg_button_title, true)
            toggleButton(R.id.btn_select_labtests_til, R.id.image3, true)
            toggleButton(R.id.btn_update_labresults_til, R.id.image4, true)
            toggleButton(R.id.btn_make_appointment_til, R.id.image5, true)
            toggleImage(1, true)
            toggleImage(2, true)
            toggleImage(3, true)
            toggleImage(4, true)


            views.btnProfileInformation.clickWithThrottle {
                viewModel.handle(RegistrationActions.RequirePersonalInfo)
            }
            views.btnPrepRegistration.clickWithThrottle {
                viewModel.handle(RegistrationActions.RequirePrepRegistration)
            }
            views.btnSelectLabtests.clickWithThrottle {
                viewModel.handle(RegistrationActions.SelectLabTests)
            }
            views.btnUpdateLabresults.clickWithThrottle {
                viewModel.handle(RegistrationActions.InputLabResults)
            }
            views.btnMakeAppointment.clickWithThrottle {
                viewModel.handle(RegistrationActions.MakeAnAppointment)
            }
            if (patientInfo.encounter?.appointment != null) {
                toggleButton(R.id.btn_join_conference_til, R.id.image6, true)
                views.btnJoinConference.clickWithThrottle {
                    val intent = Intent(requireActivity(), ConferenceActivity::class.java).apply {
                        putExtra("conferenceId", patientInfo.encounter.appointment.id)
                    }
                    startActivity(intent)
                }
                toggleButton(
                    R.id.btn_medical_examination_til,
                    R.id.image7,
                    true
                )
                toggleImage(5, true)
                toggleImage(6, true)
                views.btnMedicalExamination.clickWithThrottle {
                    viewModel.handle(RegistrationActions.MedicalExamination)
                }
            } else {
                views.btnJoinConference.isEnabled = false
                views.btnMedicalExamination.isEnabled = false
                toggleImage(5, false)
                toggleImage(6, false)
                toggleButton(R.id.btn_join_conference_til, R.id.image6, false)
                toggleButton(
                    R.id.btn_medical_examination_til,
                    R.id.image7,
                    false
                )
            }
        }
    }

    private fun initButtons() {
        // grayed out all buttons
        toggleButton(R.id.btn_profile_information_til, R.id.image1, false)
        toggleButton(R.id.btn_prep_registration_til, R.id.image2, false)
        toggleButton(R.id.btn_select_labtests_til, R.id.image3, false)
        toggleButton(R.id.btn_update_labresults_til, R.id.image4, false)
        toggleButton(R.id.btn_make_appointment_til, R.id.image5, false)
        toggleButton(R.id.btn_join_conference_til, R.id.image6, false)
        toggleButton(R.id.btn_medical_examination_til, R.id.image7, false)

        // remove all on click listeners
        views.btnProfileInformation.setOnClickListener(null)
        views.btnPrepRegistration.setOnClickListener(null)
        views.btnSelectLabtests.setOnClickListener(null)
        views.btnUpdateLabresults.setOnClickListener(null)
        views.btnMakeAppointment.setOnClickListener(null)
        views.btnJoinConference.setOnClickListener(null)
        views.btnMedicalExamination.setOnClickListener(null)
        toggleImage(1, false)
        toggleImage(2, false)
        toggleImage(3, false)
        toggleImage(4, false)
        toggleImage(5, false)
        toggleImage(6, false)
    }

    private fun toggleImage(imageId: Int, enable: Boolean) {
        val map = mapOf(
            1 to R.id.icon1,
            2 to R.id.icon2,
            3 to R.id.icon3,
            4 to R.id.icon4,
            5 to R.id.icon5,
            6 to R.id.icon6
        )
        val root = views.root
        val theme = requireActivity().theme
        var background =
            ResourcesCompat.getDrawable(resources, R.drawable.vecter_line_disable, theme)
        var alpha = 0.7f
        if (enable) {
            background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.vecter_line,
                    theme
                )
            alpha = 1.0f

        }
        map[imageId]?.let {
            root.findViewById<AppCompatImageView>(it).apply {
                this.setImageDrawable(background)
                this.alpha = alpha
            }
        }
        // button background
    }

    private fun toggleButton(btnId: Int, imageId: Int, enable: Boolean) {
        val root = views.root
        val theme = requireActivity().theme

        var background =
            ResourcesCompat.getDrawable(resources, R.drawable._button_bg_3_disabled, theme)
        var backgroundImage =
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.circular_bordered_button_disable,
                theme
            )
        var alpha = 0.7f
        if (enable) {
            background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable._button_bg_3,
                    theme
                )
            backgroundImage =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circular_bordered_button,
                    theme
                )
            alpha = 1.0f


        }

        // button background
        root.findViewById<RelativeLayout>(btnId).apply {
            this.background = background
            this.alpha = alpha
        }
        root.findViewById<RelativeLayout>(imageId).apply {
            this.background = backgroundImage
            this.alpha = alpha
        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()
        queryPatientHandled = false
    }
}
