package im.vector.app.ext.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.GlobitsFragmentAboutNoticesBinding
import im.vector.app.features.settings.VectorSettingsUrls

class ThirdPartyNoticeFragment : VectorBaseFragment<GlobitsFragmentAboutNoticesBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsFragmentAboutNoticesBinding {
        return GlobitsFragmentAboutNoticesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.webview.loadUrl(VectorSettingsUrls.THIRD_PARTY_LICENSES)
    }
}
