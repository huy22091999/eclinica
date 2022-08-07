package im.vector.app.ext.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.GlobitsFragmentAboutBinding
import im.vector.app.ext.utils.clickWithThrottle

class AboutFragment : VectorBaseFragment<GlobitsFragmentAboutBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsFragmentAboutBinding {
        return GlobitsFragmentAboutBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.version.setText(getString(R.string.version, BuildConfig.VERSION_NAME))

        setupClickListeners()
    }

    private fun setupClickListeners() {

        views.thirdPartyNotices.clickWithThrottle {
            (requireActivity() as AboutActivity).openThirdPartyNotices()
        }

        views.contactBusiness.clickWithThrottle {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:01234567890"))
            startActivity(intent)
        }

        views.contactTechnical.clickWithThrottle {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:01234567890"))
            startActivity(intent)
        }
    }
}
