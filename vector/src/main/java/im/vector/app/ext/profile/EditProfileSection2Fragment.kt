package im.vector.app.ext.profile

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.databinding.GlobitsProfileEditSection2Binding

class EditProfileSection2Fragment : EditProfileBaseFragment<GlobitsProfileEditSection2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsProfileEditSection2Binding {
        return GlobitsProfileEditSection2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
    }

    /////////////////////// PRIVATE ////////////////////////////
    private fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            views.content.justificationMode = JUSTIFICATION_MODE_INTER_WORD
        }
    }

    private fun setupClickListeners() {
        views.chkClientConcur.setOnCheckedChangeListener { _, _ ->
            // TODO complete this
        }
    }

    override fun updateData() {
        TODO("Not yet implemented")
    }
}
