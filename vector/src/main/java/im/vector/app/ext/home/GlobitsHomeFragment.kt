package im.vector.app.ext.home

import android.content.Intent
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.airbnb.mvrx.*
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.GlobitsFragmentHomeBinding
import im.vector.app.ext.GlobitsHomeActivity
import im.vector.app.ext.encounter.EncounterActivity
import im.vector.app.ext.examination.ExaminationActivity
import im.vector.app.ext.profile.ProfileActivity
import im.vector.app.ext.registration.RegistrationActivity
import im.vector.app.ext.utils.clickWithThrottle
import im.vector.app.features.usercode.UserCodeActivity
import im.vector.app.features.workers.signout.SignOutUiWorker
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import javax.inject.Inject

class GlobitsHomeFragment @Inject constructor(
    val globitsHomeViewModelFactory: GlobitsHomeViewModel.Factory,
    private val session: Session
) : VectorBaseFragment<GlobitsFragmentHomeBinding>() {

    private val viewModel: GlobitsHomeViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsFragmentHomeBinding {
        return GlobitsFragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun getMenuRes() = R.menu._globits_menu_home

    override fun onResume() {
        super.onResume()
        viewModel.handle(GlobitsHomeActions.GetStepMessage)
    }

    override fun invalidate() = withState(viewModel) {
        updateWithState(it)
    }

    private fun updateWithState(state: GlobitsHomeViewState) {
        setupClickListeners(state)
        setupStepMessage(state)
    }

    private fun setupStepMessage(state: GlobitsHomeViewState) {
        if (state.stepMessage is Success) {
            val stepMessage = state.stepMessage.invoke()
            views.stepMessage.text = stepMessage.message
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // Change toolbar icon tint
        val color = resources.getColor(R.color.alpha_white_50, requireActivity().theme)
        val itemQR = menu.findItem(R.id.qr_code)

        val icon = ResourcesCompat.getDrawable(resources, R.drawable.qr_code, null)
        icon?.setTint(color)

        itemQR.icon = icon
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.qr_code -> {
                startActivity(UserCodeActivity.newIntent(requireContext(), session.myUserId))
                true
            }

            R.id.action_about -> {
                (requireActivity() as GlobitsHomeActivity).openAbout()
                true
            }
            R.id.chat -> {
                (requireActivity() as GlobitsHomeActivity).openChat()
                true
            }
//
//            R.id.action_thirdparty_notices -> {
//                activity?.displayInWebView(VectorSettingsUrls.THIRD_PARTY_LICENSES)
//                true
//            }

//            R.id.action_settings -> {
//                (requireActivity() as GlobitsHomeActivity).openSettings()
//                true
//            }

            R.id.action_logout -> {
                SignOutUiWorker(requireActivity()).perform()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---------------------------------------
    // private
    // ---------------------------------------
    private fun setupClickListeners(state: GlobitsHomeViewState) {

        initButtons()

        if (state.loginPatient is Success) {
            val patient = state.loginPatient.invoke()

            Timber.e(
                "-----------> Current treatment status = %d, newUpdate = %s",
                patient.treatmentStatus,
                patient.infoUpdated.toString()
            )

            // main functions
            views.btnSignupService.clickWithThrottle {
                val intent = Intent(requireActivity(), RegistrationActivity::class.java)
                startActivity(intent)
            }

//            when {
//                patient.treatmentStatus != null && patient.treatmentStatus >= 3 ->
//                    views.btnAppointment.clickWithThrottle {
//                        val intent = Intent(requireActivity(), AppointmentActivity::class.java)
//                        startActivity(intent)
//                    }
//
//
//                patient.treatmentStatus != null && patient.treatmentStatus >= 2 ->
//                    views.btnLabTests.clickWithThrottle {
//                        val intent = Intent(requireActivity(), LaboratoryActivity::class.java)
//                        startActivity(intent)
//                    }
//
//                patient.treatmentStatus != null && patient.treatmentStatus >= 6 ->
//                    views.btnPrescription.clickWithThrottle {
//                        val intent = Intent(requireActivity(), PrescriptionActivity::class.java)
//                        startActivity(intent)
//                    }
//
//                patient.treatmentStatus != null && patient.treatmentStatus >= 0 ->
//                    views.btnPersonalProfile.clickWithThrottle {
//                        val intent = Intent(requireActivity(), ProfileActivity::class.java)
//                        startActivity(intent)
//                    }
//            }

            if (patient.treatmentStatus != null && patient.treatmentStatus >= 0) {
                views.btnPersonalProfile.clickWithThrottle {
                    val intent = Intent(requireActivity(), ProfileActivity::class.java)
                    startActivity(intent)
                }
            } else {
                toggleButton(
                    R.id.btn_personal_profile,
                    R.id.profile_icon,
                    R.drawable._button_bg_br,
                    R.drawable._button_bg_br_disabled,
                    false
                )
            }

            views.btnAppointment.clickWithThrottle {
                val intent = Intent(requireActivity(), EncounterActivity::class.java)
                startActivity(intent)
            }

            views.btnEncounterLabtest.clickWithThrottle {
                val intent = Intent(requireActivity(), EncounterActivity::class.java)
                startActivity(intent)
            }

            views.btnMedicalExamination.clickWithThrottle {
                val intent = Intent(requireActivity(), ExaminationActivity::class.java)
                startActivity(intent)
            }

//            views.btnPersonalProfile.clickWithThrottle {
//                val intent = Intent(requireActivity(), ProfileActivity::class.java)
//                startActivity(intent)
//            }
        }

    }

    private fun initButtons() {
//        toggleButton(
//            R.id.btn_appointment,
//            R.id.appointment_icon,
//            R.drawable._button_bg_tl,
//            R.drawable._button_bg_tl_disabled,
//            false
//        )
//        toggleButton(
//            R.id.btn_lab_tests,
//            R.id.lab_test_icon,
//            R.drawable._button_bg_tr,
//            R.drawable._button_bg_tr_disabled,
//            false
//        )
//        toggleButton(
//            R.id.btn_prescription,
//            R.id.drug_prescription_icon,
//            R.drawable._button_bg_bl,
//            R.drawable._button_bg_bl_disabled,
//            false
//        )

        views.btnAppointment.setOnClickListener(null)
        views.btnEncounterLabtest.setOnClickListener(null)
        views.btnPrescription.setOnClickListener(null)
        views.btnPersonalProfile.setOnClickListener(null)
    }

    private fun toggleButton(btnId: Int, iconId: Int, enabledBgId: Int, disabledBgId: Int, enable: Boolean) {
        val root = views.root
        val theme = requireActivity().theme

        var background = ResourcesCompat.getDrawable(resources, disabledBgId, theme)
        var imageTint = ResourcesCompat.getColor(resources, R.color.riot_primary_text_color_disabled_light, theme)

        if (enable) {
            background =
                ResourcesCompat.getDrawable(
                    resources,
                    enabledBgId,
                    theme
                )

            imageTint = ResourcesCompat.getColor(
                resources,
                R.color.riotx_accent,
                theme
            )
        }

        // button background
        root.findViewById<LinearLayout>(btnId).apply {
            this.background = background
        }

        // button text color
        root.findViewById<AppCompatImageView>(iconId).apply {
            setColorFilter(imageTint)
        }
    }
}
