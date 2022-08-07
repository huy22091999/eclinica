package im.vector.app.ext.encounter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import im.vector.app.ext.laboratory.LaboratoryFragment

class EncounterAndLaboratoryPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val TAB_COUNT = 2
    }

    private val fragments = mutableListOf<Fragment>()

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    fun getFragment(position: Int): Fragment {
        return fragments.get(position)
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> EncounterFragment()
            else -> LaboratoryFragment()
        }

        fragments.add(position, fragment)
        return fragment
    }
}
