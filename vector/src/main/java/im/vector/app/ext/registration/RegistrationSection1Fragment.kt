package im.vector.app.ext.registration


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.databinding.GlobitsRegSection1Binding
import im.vector.app.ext.data.model.PatientInfo
import im.vector.app.ext.data.model.PatientPeriodPrepScreen
import im.vector.app.ext.registration.custom.RegistrationDialog
import im.vector.app.ext.registration.custom.SupportStaffDialog
import im.vector.app.ext.utils.snackbar
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject


class RegistrationSection1Fragment @Inject constructor(
    private val session: Session
) : RegistrationBaseFragment<GlobitsRegSection1Binding>() {

    private lateinit var editingScreening: PatientPeriodPrepScreen

    override var toolbarTitleRes: Int? = R.string.prep_registration_title

    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    private lateinit var patient: PatientInfo


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsRegSection1Binding {
        return GlobitsRegSection1Binding.inflate(inflater, container, false)
    }

    companion object {
        const val TAG = "_REGISTRATION_REGISTER_PREP"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupViews()
        RegistrationDialog.createDialogHelp(
            requireContext(),
            getString(R.string.help),
            getString(R.string.is_help)
        )
        {
            viewModel.handle(RegistrationActions.CheckSupportStaffOnline)
        }
        viewModel.observeViewEvents {
            handleViewEvents(it)
        }
    }

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)
        if (state.asyncPatientInfo is Success) {
            if (!this::patient.isInitialized) {
                state.asyncPatientInfo.invoke()?.let {
                    patient = it

                    updateGender()

                }
            }
        }


        when {
            state.asyncPrepScreen is Success -> {
                state.asyncPrepScreen.invoke()?.let {
                    editingScreening = it

                    populateData()
                }
            }

            else -> Unit
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


    override fun resetViewModel() {
    }

    override fun updateData() {

    }

    /////////////////////////// PRIVATE //////////////////////////////

    private fun setupViews() {
        views.question4Title.text = getSpannedText(getString(R.string.in_the_last_6_months))
        views.question41.text = getSpannedText(getString(R.string.question_41))
        views.question411.text = getSpannedText(getString(R.string.question_411))
        views.question412.text = getSpannedText(getString(R.string.question_412))
        views.question413.text = getSpannedText(getString(R.string.question_413))
        views.question414.text = getSpannedText(getString(R.string.question_414))
        views.question415.text = getSpannedText(getString(R.string.question_415))
        views.question5.text = getSpannedText(getString(R.string.question_5))
        views.question6.text = getSpannedText(getString(R.string.question_6))
        viewModel.handle(RegistrationActions.QueryPatientInfoData)

    }

    private fun getSpannedText(text: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }

    private fun showDialog(group: RadioGroup, i: Int) {
        group.setOnCheckedChangeListener { _, p1 ->
            when (p1) {
                i -> {
                    createDialog(group, i)
                }
            }
        }
    }


    private fun createDialog(r: RadioGroup, i: Int) {
        dialogBuilder = AlertDialog.Builder(requireActivity())
        val eligiblePopup: View = layoutInflater.inflate(R.layout.dialog_accpet, null)

        val btnAgree = eligiblePopup.findViewById<Button>(R.id.btnAccpet)
        val btnClose = eligiblePopup.findViewById<Button>(R.id.btnCancel)

        dialogBuilder.setView(eligiblePopup)
        btnClose.setOnClickListener {
            r.setOnCheckedChangeListener(null)
            r.clearCheck()
            showDialog(r, i)
            dialog.cancel()

        }

        btnAgree.setOnClickListener {
            dialog.dismiss()

        }

        dialog = dialogBuilder.create()
        dialog.show()

    }

    private fun handleViewEvents(event: RegistrationViewEvents) {
        when (event) {
            is RegistrationViewEvents.CheckSupportOnlineMessage -> {
                withState(viewModel) {
                    when (it.supportOnline) {
                        is Success -> {
                            it.supportOnline.invoke()?.let {
                                if (it) {

                                } else  Toast.makeText(
                                    context,
                                    getString(R.string.support_staff_offline),
                                    Toast.LENGTH_LONG
                                ).show()
                                SupportStaffDialog().show(parentFragmentManager, TAG)
                            }
                        }
                        else -> Unit
                    }
                }
            }
            is RegistrationViewEvents.SaveScreeningFormComplete -> {
                withState(viewModel) {
                    when (it.asyncEditingPrepScreen) {
                        is Success -> {
                            requireActivity().snackbar(getString(R.string.data_successfully_saved))
//                            Notification(context)
//                                .showNotifi("Cảm ơn bạn đã điền thông tin phiếu sàng lọc. Chúng tôi sẽ phản hồi sớm thông tin")
                            RegistrationDialog.createDialog(
                                requireContext(), "CẬP NHẬT PHIẾU SÀNG LỌC THÀNH CÔNG!",
                                "Cảm ơn bạn đã điền phiếu đánh giá sàng lọc trước PrEP.\n" +
                                        "PK sẽ liên hệ với bạn để thông báo bước tiếp theo.\n" +
                                        "Xin cảm ơn!", this@RegistrationSection1Fragment
                            )
                        }

                        is Fail -> {
                            requireActivity().snackbar(getString(R.string.data_save_failure))
                        }

                        else -> Unit
                    }
                }

            }
            else -> Unit
        }
    }

    private fun updateGender() {
        if (!this::patient.isInitialized || patient.patientId == null) {
            Log.e("gender", "null")

            return
        }
        with(patient) {
            var gender = this.gender?.toString()
            if (gender.equals("M")) {
                views.question1Group.check(R.id.question_1_ans_1)
            } else if (gender == "F") {
                views.question1Group.check(R.id.question_1_ans_2)

            }
        }
    }

    private fun populateData() {
        if (!this::editingScreening.isInitialized || editingScreening.id == null) {
            showDialog(views.question5Group, R.id.question_5_ans_2)
            showDialog(views.question6Group, R.id.question_6_ans_2)

            return
        }

        editingScreening.question1?.let {
            when (it) {
                0 -> views.question1Group.check(R.id.question_1_ans_1)
                1 -> views.question1Group.check(R.id.question_1_ans_2)
            }
        }


        editingScreening.question2?.let {
            when (it) {
                0 -> views.question2Group.check(R.id.question_2_ans_1)
                1 -> views.question2Group.check(R.id.question_2_ans_2)
                2 -> views.question2Group.check(R.id.question_2_ans_3)
                3 -> views.question2Group.check(R.id.question_2_ans_4)
                4 -> views.question2Group.check(R.id.question_2_ans_5)
            }
        }

        editingScreening.question3?.let {
            when (it) {
                0 -> views.question3Group.check(R.id.question_3_ans_1)
                1 -> views.question3Group.check(R.id.question_3_ans_2)
                2 -> views.question3Group.check(R.id.question_3_ans_3)
            }
        }

        editingScreening.question41?.let {
            when (it) {
                0 -> views.question41Group.check(R.id.question_41_ans_1)
                1 -> views.question41Group.check(R.id.question_41_ans_2)
            }
        }

        editingScreening.question42?.let {
            when (it) {
                0 -> views.question42Group.check(R.id.question_42_ans_1)
                1 -> views.question42Group.check(R.id.question_42_ans_2)
            }
        }

        editingScreening.question5?.let {
            when (it) {
                0 -> {
                    views.question5Group.check(R.id.question_5_ans_1)
                }
                1 -> {
                    views.question5Group.check(R.id.question_5_ans_2)
                }


            }


            showDialog(views.question5Group, R.id.question_5_ans_2)

        }
        if (editingScreening.question5 == null) {
            showDialog(views.question5Group, R.id.question_5_ans_2)
        }
        editingScreening.question6?.let {
            when (it) {
                0 -> {
                    views.question6Group.check(R.id.question_6_ans_1)
//                    views.question6DetailsTil.visible(false)
                }
                1 -> {
                    views.question6Group.check(R.id.question_6_ans_2)
//                    views.question6DetailsTil.visible(true)
                }
            }
            showDialog(views.question6Group, R.id.question_6_ans_2)

        }
        if (editingScreening.question6 == null) {
            showDialog(views.question6Group, R.id.question_6_ans_2)
        }

        editingScreening.question7?.let {
            when (it) {
                0 -> views.question7Group.check(R.id.question_7_ans_1)
                1 -> views.question7Group.check(R.id.question_7_ans_2)
            }
        }
    }

    private fun saveData() {
        // update data from views
        if (views.question1Group.checkedRadioButtonId == -1 || views.question2Group.checkedRadioButtonId == -1
            || views.question3Group.checkedRadioButtonId == -1 || views.question41Group.checkedRadioButtonId == -1
            || views.question42Group.checkedRadioButtonId == -1 || views.question5Group.checkedRadioButtonId == -1
            || views.question6Group.checkedRadioButtonId == -1 || views.question7Group.checkedRadioButtonId == -1
        ) {
            Toast.makeText(context, "Tất cả không được để trống", Toast.LENGTH_LONG).show()
            return
        }
        with(editingScreening) {

            this.question1 = when (views.question1Group.checkedRadioButtonId) {
                R.id.question_1_ans_1 -> 0
                R.id.question_1_ans_2 -> 1
                else -> null
            }

            this.question2 = when (views.question2Group.checkedRadioButtonId) {
                R.id.question_2_ans_1 -> 0
                R.id.question_2_ans_2 -> 1
                R.id.question_2_ans_3 -> 2
                R.id.question_2_ans_4 -> 3
                R.id.question_2_ans_5 -> 4
                else -> null
            }

            this.question3 = when (views.question3Group.checkedRadioButtonId) {
                R.id.question_3_ans_1 -> 0
                R.id.question_3_ans_2 -> 1
                R.id.question_3_ans_3 -> 2
                else -> null
            }

            this.question41 = when (views.question41Group.checkedRadioButtonId) {
                R.id.question_41_ans_1 -> 0
                R.id.question_41_ans_2 -> 1
                else -> null
            }

            this.question42 = when (views.question42Group.checkedRadioButtonId) {
                R.id.question_42_ans_1 -> 0
                R.id.question_42_ans_2 -> 1
                else -> null
            }

            this.question5 = when (views.question5Group.checkedRadioButtonId) {
                R.id.question_5_ans_1 -> 0
                R.id.question_5_ans_2 -> 1
                else -> null
            }

            this.question6 = when (views.question6Group.checkedRadioButtonId) {
                R.id.question_6_ans_1 -> 0
                R.id.question_6_ans_2 -> 1
                else -> null
            }

            this.question7 = when (views.question7Group.checkedRadioButtonId) {
                R.id.question_7_ans_1 -> 0
                R.id.question_7_ans_2 -> 1
                else -> null
            }
        }



        viewModel.handle(RegistrationActions.SaveScreeningResult(editingScreening))
    }
}
