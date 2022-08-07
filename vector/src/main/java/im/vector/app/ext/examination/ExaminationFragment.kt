package im.vector.app.ext.examination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.Success
import com.google.android.material.tabs.TabLayoutMediator
import im.vector.app.R
import im.vector.app.databinding.GlobitsFragmentExaminationBinding
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.encounter.EncounterActivity
import im.vector.app.ext.examination.list.ExaminationPagerAdapter
import javax.inject.Inject

class ExaminationFragment @Inject constructor() :
    ExaminationBaseFragment<GlobitsFragmentExaminationBinding>() {

    override var toolbarTitleRes: Int? = R.string.drug_prescriptions
    private lateinit var adapterFragment: ExaminationPagerAdapter
    private lateinit var prescriptionPage: Page<Prescription>
    private lateinit var viewPager:ViewPager2
    override fun onResume() {
        super.onResume()
        viewModel.handle(ExaminationAction.QueryExamination)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsFragmentExaminationBinding {
        return GlobitsFragmentExaminationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    override fun updateWithState(state: ExaminationViewState) {
        if (state.asyncPagePrescriptions is Success) {
            state.asyncPagePrescriptions.invoke().let {
                prescriptionPage = it
            }
        }
    }

    private fun setupViewPager() {
        adapterFragment = ExaminationPagerAdapter(this)
        viewPager = views.container
        viewPager.adapter = adapterFragment
        viewPager.offscreenPageLimit = 2

        // tabs
        val tabNames = listOf(
            getString(R.string.encouter),
            getString(R.string.laboratory),
        )

        val tabLayout = views.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }
}
