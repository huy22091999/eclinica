package im.vector.app.ext.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.databinding.GlobitsProfileEditSection0Binding
import im.vector.app.databinding.GlobitsProfileEditSection1Binding
import im.vector.app.databinding.GlobitsRegSection1Binding
import im.vector.app.ext.data.model.PatientPeriodPrepScreen
import im.vector.app.ext.registration.RegistrationActions
import im.vector.app.ext.registration.RegistrationViewState
import im.vector.app.ext.utils.toStringAlt
import im.vector.app.ext.utils.visible

class EditProfileSection1Fragment : EditProfileBaseFragment<GlobitsRegSection1Binding>() {

    private lateinit var editingScreening: PatientPeriodPrepScreen

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsRegSection1Binding {
        return GlobitsRegSection1Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setupViews()
        setupradiobutton()


//        viewModel.observeViewEvents {
//            handleViewEvents(it)
//        }
    }

    private fun setupradiobutton(){
        views.question1Ans1.isEnabled = false
        views.question1Ans2.isEnabled = false
        views.question2Ans1.isEnabled = false
        views.question2Ans2.isEnabled = false
        views.question2Ans3.isEnabled = false
        views.question2Ans4.isEnabled = false
        views.question2Ans5.isEnabled = false
        views.question3Ans1.isEnabled = false
        views.question3Ans2.isEnabled = false
        views.question3Ans3.isEnabled = false
        views.question41Ans1.isEnabled = false
        views.question41Ans2.isEnabled = false
        views.question5Ans1.isEnabled = false
        views.question5Ans2.isEnabled = false
        views.question6Ans1.isEnabled = false
        views.question6Ans2.isEnabled = false
        views.question7Ans1.isEnabled = false
        views.question7Ans2.isEnabled = false

    }

    override fun updateWithState(state: ProfileViewState) {
        super.updateWithState(state)

        if (state.asyncPatientInfo is Success) {
            if (!this::editingScreening.isInitialized) {
                state.asyncPatientInfo.invoke()?.let {
                    viewModel.handle(ProfileAction.QueryPeriodPrepScreen(it.lastPatientPrepScreeningId))
                }
            }
        }

        if (state.asyncPrepScreen is Success) {
            if (!this::editingScreening.isInitialized) {
                state.asyncPrepScreen.invoke()?.let {
                    editingScreening = it

                    populateData()
                }
            }
        }

    }

    private fun setupViews() {
//        views.sourceDetailTil.visible(false)
//        views.clientTypeGroup.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.ctype_walkin -> {
//                    views.sourceDetailTil.visible(false)
//                    views.sourceDetail.setText(null)
//                }
//                else -> views.sourceDetailTil.visible(true)
//            }
//        }
//
//        views.question5DetailsTil.visible(false)
//        views.question5Group.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.question_5_ans_2 -> views.question5DetailsTil.visible(true)
//                else -> {
//                    views.question5DetailsTil.visible(false)
//                    views.question51Group.clearCheck()
//                    views.question52Group.clearCheck()
//                    views.question53Group.clearCheck()
//                    views.question54Group.clearCheck()
//                    views.question55Group.clearCheck()
//                }
//            }
//        }
//
//        views.question6DetailsTil.visible(false)
//        views.question6Group.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.question_6_ans_2 -> views.question6DetailsTil.visible(true)
//                else -> {
//                    views.question6DetailsTil.visible(false)
//                    views.question61Group.clearCheck()
//                }
//            }
//        }
    }

    private fun populateData() {
        if (!this::editingScreening.isInitialized || editingScreening.id == null) {
            return
        }

        // Client type
//        editingScreening.source?.let {
//            when (it) {
//                0 -> {
//                    views.clientTypeGroup.check(R.id.ctype_walkin)
//                    views.sourceDetailTil.visible(false)
//                }
//                1 -> {
//                    views.clientTypeGroup.check(R.id.ctype_facility_refer)
//
//                    views.sourceDetailTil.visible(true)
//                    views.sourceDetailTil.hint = getString(R.string.please_specify_facility_name)
//                    views.sourceDetail.setText(editingScreening.facilityName)
//                }
//                2 -> {
//                    views.clientTypeGroup.check(R.id.ctype_cbo_refer)
//
//                    views.sourceDetailTil.visible(true)
//                    views.sourceDetailTil.hint = getString(R.string.please_specify_group_name)
//                    views.sourceDetail.setText(editingScreening.groupName)
//                }
//                3 -> {
//                    views.clientTypeGroup.check(R.id.ctype_other)
//
//                    views.sourceDetailTil.visible(true)
//                    views.sourceDetailTil.hint = getString(R.string.please_specify)
//                    views.sourceDetail.setText(editingScreening.otherSouces)
//                }
//            }
//        }

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

//        editingScreening.question31?.let {
//            when (it) {
//                0 -> views.question31Group.check(R.id.question_3_1_ans_1)
//                1 -> views.question31Group.check(R.id.question_3_1_ans_2)
//            }
//        }
//
//        editingScreening.question32?.let {
//            when (it) {
//                0 -> views.question32Group.check(R.id.question_3_2_ans_1)
//                1 -> views.question32Group.check(R.id.question_3_2_ans_2)
//            }
//        }

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
//                    views.question5DetailsTil.visible(false)
                }
                1 -> {
                    views.question5Group.check(R.id.question_5_ans_2)
//                    views.question5DetailsTil.visible(true)
                }
//                2 -> {
//                    views.question5Group.check(R.id.question_5_ans_3)
//                    views.question5DetailsTil.visible(false)
//                }
            }
        }

        // question 5 details (5.1 -> 5.5)
//        editingScreening.question51?.let {
//            when (it) {
//                0 -> views.question51Group.check(R.id.question_5_1_ans_1)
//                1 -> views.question51Group.check(R.id.question_5_1_ans_2)
//                2 -> views.question51Group.check(R.id.question_5_1_ans_3)
//            }
//        }
//
//        editingScreening.question52?.let {
//            when (it) {
//                0 -> views.question52Group.check(R.id.question_5_2_ans_1)
//                1 -> views.question52Group.check(R.id.question_5_2_ans_2)
//                2 -> views.question52Group.check(R.id.question_5_2_ans_3)
//            }
//        }
//
//        editingScreening.question53?.let {
//            when (it) {
//                0 -> views.question53Group.check(R.id.question_5_3_ans_1)
//                1 -> views.question53Group.check(R.id.question_5_3_ans_2)
//                2 -> views.question53Group.check(R.id.question_5_3_ans_3)
//            }
//        }
//
//        editingScreening.question54?.let {
//            when (it) {
//                0 -> views.question54Group.check(R.id.question_5_4_ans_1)
//                1 -> views.question54Group.check(R.id.question_5_4_ans_2)
//                2 -> views.question54Group.check(R.id.question_5_4_ans_3)
//            }
//        }
//
//        editingScreening.question55?.let {
//            when (it) {
//                0 -> views.question55Group.check(R.id.question_5_5_ans_1)
//                1 -> views.question55Group.check(R.id.question_5_5_ans_2)
//                2 -> views.question55Group.check(R.id.question_5_5_ans_3)
//            }
//        }

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
                2 -> {
                    views.question6Group.check(R.id.question_6_ans_3)
//                    views.question6DetailsTil.visible(false)
                }
            }
        }

//        editingScreening.question61?.let {
//            when (it) {
//                0 -> views.question61Group.check(R.id.question_6_1_ans_1)
//                1 -> views.question61Group.check(R.id.question_6_1_ans_2)
//                2 -> views.question61Group.check(R.id.question_6_1_ans_3)
//            }
//        }

        editingScreening.question7?.let {
            when (it) {
                0 -> views.question7Group.check(R.id.question_7_ans_1)
                1 -> views.question7Group.check(R.id.question_7_ans_2)
                2 -> views.question7Group.check(R.id.question_7_ans_3)
            }
        }

//        editingScreening.question8?.let {
//            when (it) {
//                0 -> views.question8Group.check(R.id.question_8_ans_1)
//                1 -> views.question8Group.check(R.id.question_8_ans_2)
//                2 -> views.question8Group.check(R.id.question_8_ans_3)
//            }
//        }
//
//        editingScreening.question9?.let {
//            when (it) {
//                0 -> views.question9Group.check(R.id.question_9_ans_1)
//                1 -> views.question9Group.check(R.id.question_9_ans_2)
//                2 -> views.question9Group.check(R.id.question_9_ans_3)
//            }
//        }
//
//        editingScreening.question10?.let {
//            when (it) {
//                0 -> views.question10Group.check(R.id.question_10_ans_1)
//                1 -> views.question10Group.check(R.id.question_10_ans_2)
//                2 -> views.question10Group.check(R.id.question_10_ans_3)
//            }
//        }
//
//        editingScreening.question11?.let {
//            when (it) {
//                0 -> views.question11Group.check(R.id.question_11_ans_1)
//                1 -> views.question11Group.check(R.id.question_11_ans_2)
//                2 -> views.question11Group.check(R.id.question_11_ans_3)
//            }
//        }
//
//        editingScreening.question12?.let {
//            when (it) {
//                0 -> views.question12Group.check(R.id.question_12_ans_1)
//                1 -> views.question12Group.check(R.id.question_12_ans_2)
//                2 -> views.question12Group.check(R.id.question_12_ans_3)
//            }
//        }
//
//        // Risk groups
//        editingScreening.riskGroups?.let {
//            for (risk in it) {
//                when (risk) {
//                    0 -> views.chkMsm.isChecked = true
//                    1 -> views.chkTransgenderFemale.isChecked = true
//                    2 -> views.chkTransgenderMale.isChecked = true
//                    3 -> views.chkMultiPartners.isChecked = true
//                    4 -> views.chkPwid.isChecked = true
//                    5 -> views.chkSexWorker.isChecked = true
//                    6 -> views.chkPartnerPlhiv.isChecked = true
//                    7 -> views.chkPrepRequired.isChecked = true
//                }
//            }
//        }
    }

    private fun saveData() {
        // update data from views
        with(editingScreening) {
//            this.source = when (views.clientTypeGroup.checkedRadioButtonId) {
//                R.id.ctype_walkin -> 0
//                R.id.ctype_facility_refer -> {
//                    this.facilityName = views.sourceDetail.toStringAlt()
//                    1
//                }
//                R.id.ctype_cbo_refer -> {
//                    this.groupName = views.sourceDetail.toStringAlt()
//                    2
//                }
//                R.id.ctype_other -> {
//                    this.otherSouces = views.sourceDetail.toStringAlt()
//                    3
//                }
//                else -> null
//            }

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

//            this.question31 = when (views.question31Group.checkedRadioButtonId) {
//                R.id.question_3_1_ans_1 -> 0
//                R.id.question_3_1_ans_2 -> 1
//                else -> null
//            }
//
//            this.question32 = when (views.question32Group.checkedRadioButtonId) {
//                R.id.question_3_2_ans_1 -> 0
//                R.id.question_3_2_ans_2 -> 1
//                else -> null
//            }

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
//                R.id.question_5_ans_3 -> 2
                else -> null
            }

//            this.question51 = when (views.question51Group.checkedRadioButtonId) {
//                R.id.question_5_1_ans_1 -> 0
//                R.id.question_5_1_ans_2 -> 1
//                R.id.question_5_1_ans_3 -> 1
//                else -> null
//            }
//
//            this.question52 = when (views.question52Group.checkedRadioButtonId) {
//                R.id.question_5_2_ans_1 -> 0
//                R.id.question_5_2_ans_2 -> 1
//                R.id.question_5_2_ans_3 -> 2
//                else -> null
//            }
//
//            this.question53 = when (views.question53Group.checkedRadioButtonId) {
//                R.id.question_5_3_ans_1 -> 0
//                R.id.question_5_3_ans_2 -> 1
//                R.id.question_5_3_ans_3 -> 2
//                else -> null
//            }
//
//            this.question54 = when (views.question54Group.checkedRadioButtonId) {
//                R.id.question_5_4_ans_1 -> 0
//                R.id.question_5_4_ans_2 -> 1
//                R.id.question_5_4_ans_3 -> 2
//                else -> null
//            }
//
//            this.question55 = when (views.question55Group.checkedRadioButtonId) {
//                R.id.question_5_5_ans_1 -> 0
//                R.id.question_5_5_ans_2 -> 1
//                R.id.question_5_5_ans_3 -> 2
//                else -> null
//            }

            this.question6 = when (views.question6Group.checkedRadioButtonId) {
                R.id.question_6_ans_1 -> 0
                R.id.question_6_ans_2 -> 1
                R.id.question_6_ans_3 -> 2
                else -> null
            }

//            this.question61 = when (views.question61Group.checkedRadioButtonId) {
//                R.id.question_6_1_ans_1 -> 0
//                R.id.question_6_1_ans_2 -> 1
//                R.id.question_6_1_ans_3 -> 2
//                else -> null
//            }

            this.question7 = when (views.question7Group.checkedRadioButtonId) {
                R.id.question_7_ans_1 -> 0
                R.id.question_7_ans_2 -> 1
                R.id.question_7_ans_3 -> 2
                else -> null
            }

//            this.question8 = when (views.question8Group.checkedRadioButtonId) {
//                R.id.question_8_ans_1 -> 0
//                R.id.question_8_ans_2 -> 1
//                R.id.question_8_ans_3 -> 2
//                else -> null
//            }
//
//            this.question9 = when (views.question9Group.checkedRadioButtonId) {
//                R.id.question_9_ans_1 -> 0
//                R.id.question_9_ans_2 -> 1
//                R.id.question_9_ans_3 -> 2
//                else -> null
//            }
//
//            this.question10 = when (views.question10Group.checkedRadioButtonId) {
//                R.id.question_10_ans_1 -> 0
//                R.id.question_10_ans_2 -> 1
//                R.id.question_10_ans_3 -> 2
//                else -> null
//            }
//
//            this.question11 = when (views.question11Group.checkedRadioButtonId) {
//                R.id.question_11_ans_1 -> 0
//                R.id.question_11_ans_2 -> 1
//                R.id.question_11_ans_3 -> 2
//                else -> null
//            }
//
//            this.question12 = when (views.question12Group.checkedRadioButtonId) {
//                R.id.question_12_ans_1 -> 0
//                R.id.question_12_ans_2 -> 1
//                R.id.question_12_ans_3 -> 2
//                else -> null
//            }
//
//            // risk groups
//            this.riskGroups = mutableListOf()
//
//            if (views.chkMsm.isChecked) {
//                this.riskGroups!!.add(0)
//            }
//
//            if (views.chkTransgenderFemale.isChecked) {
//                this.riskGroups!!.add(1)
//            }
//
//            if (views.chkTransgenderMale.isChecked) {
//                this.riskGroups!!.add(2)
//            }
//
//            if (views.chkMultiPartners.isChecked) {
//                this.riskGroups!!.add(3)
//            }
//
//            if (views.chkPwid.isChecked) {
//                this.riskGroups!!.add(4)
//            }
//
//            if (views.chkSexWorker.isChecked) {
//                this.riskGroups!!.add(5)
//            }
//
//            if (views.chkPartnerPlhiv.isChecked) {
//                this.riskGroups!!.add(6)
//            }
//
//            if (views.chkPrepRequired.isChecked) {
//                this.riskGroups!!.add(7)
//            }
        }

//        viewModel.handle(RegistrationActions.SaveScreeningResult(editingScreening))
    }

    override fun updateData() {
        TODO("Not yet implemented")
    }
}
