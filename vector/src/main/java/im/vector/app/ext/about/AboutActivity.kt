package im.vector.app.ext.about

import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import im.vector.app.R
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.utils.displayInWebView
import im.vector.app.databinding.GlobitsActivityBinding
import im.vector.app.ext.utils.makeBarsTransparent
import im.vector.app.features.settings.VectorSettingsUrls

class AboutActivity : VectorBaseActivity<GlobitsActivityBinding>() {

    override fun getBinding(): GlobitsActivityBinding {
        return GlobitsActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)
    }

    override fun initUiAndData() {
        super.initUiAndData()

        setupToolbar()

        if (isFirstCreation()) {
            addFragment(R.id.container, AboutFragment::class.java)
        }
    }

    private fun setupToolbar() {
        views.toolbar.also {
//            title = getString(R.string.about)
            setSupportActionBar(it)
        }
        views.toolbarTitle.text = getString(R.string.about)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    /////////////// PUBLIC //////////////////////
    fun openThirdPartyNotices() {
        addFragmentToBackstack(R.id.container, ThirdPartyNoticeFragment::class.java)
    }
}
