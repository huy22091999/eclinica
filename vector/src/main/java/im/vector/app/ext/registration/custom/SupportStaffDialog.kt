package im.vector.app.ext.registration.custom

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import im.vector.app.R
import im.vector.app.databinding.GlobitsDialogSupportStaffBinding
import im.vector.app.ext.registration.RegistrationSection1Fragment

class SupportStaffDialog:DialogFragment(),DialogInterface.OnClickListener {
    private lateinit var _binding:GlobitsDialogSupportStaffBinding
    private val binding:GlobitsDialogSupportStaffBinding get() = _binding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = GlobitsDialogSupportStaffBinding.inflate(LayoutInflater.from(context))
        val cancel = getString(R.string.cancel)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setNegativeButton(cancel) { _, _ -> dialog?.dismiss() }
            .create()
    }
    companion object {
        val TAG: String = SupportStaffDialog::class.java.name
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }
    override fun onClick(p0: DialogInterface?, p1: Int) {
        when (p1) {
            Dialog.BUTTON_NEGATIVE -> dialog?.dismiss()
        }
    }
}
