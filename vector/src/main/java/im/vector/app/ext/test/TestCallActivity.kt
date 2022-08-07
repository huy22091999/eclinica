package im.vector.app.ext.test

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivityTestCallBinding
import im.vector.app.ext.utils.adjustToolbarPadding
import im.vector.app.ext.utils.makeBarsTransparent
import im.vector.app.features.home.room.detail.RoomDetailActivity
import im.vector.app.features.home.room.detail.RoomDetailArgs

class TestCallActivity : VectorBaseActivity<ActivityTestCallBinding>() {

    @CallSuper
    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun getBinding(): ActivityTestCallBinding {
        return ActivityTestCallBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)

        setupToolbar()
        replaceFragment(TestCallFragment::class.java)
    }

    // set up the toolbar
    private fun setupToolbar() {
        setSupportActionBar(views.toolbar)

        views.toolbar.title = getString(R.string.toolbar_title_testcalls)
        views.toolbar.adjustToolbarPadding(this)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
//            elevation = 0F
        }
    }

    fun startChatActivity() {
        val roomDetailArgs = RoomDetailArgs(roomId = "!YqEhwXuHbtEIHnYVMg:gcom.globits.net")
        val intent = RoomDetailActivity.newIntent(this, roomDetailArgs)
        startActivity(intent)
    }

    // add the call fragment
    fun <T : Fragment> replaceFragment(fragmentClass: Class<T>) {
        if (isFirstCreation()) {
            val roomDetailArgs = RoomDetailArgs(roomId = "!YqEhwXuHbtEIHnYVMg:gcom.globits.net")
            replaceFragment(
                R.id.fragmentContainer,
                fragmentClass,
                roomDetailArgs
            )
        }
    }

}
