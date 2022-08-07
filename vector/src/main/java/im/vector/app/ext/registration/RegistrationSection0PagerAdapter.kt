package im.vector.app.ext.registration

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RegistrationSection0PagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    companion object {
        const val TAB_COUNT = 3
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
            0 -> RegistrationSection0Fragment1()
            1 -> RegistrationSection0Fragment2()
            else -> RegistrationSection0Fragment3()
        }

        fragments.add(position, fragment)
        return fragment
    }
}
