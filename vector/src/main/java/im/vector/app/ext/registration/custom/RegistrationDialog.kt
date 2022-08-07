package im.vector.app.ext.registration.custom

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.textview.MaterialTextView
import im.vector.app.R
import im.vector.app.core.utils.openUrlInChromeCustomTab
import im.vector.app.ext.GlobitsHomeActivity
import im.vector.app.features.home.HomeActivity
import im.vector.app.features.settings.VectorSettingsUrls

object RegistrationDialog {
    fun createDialog(context: Context,title :String?, content : String?,fragment: Fragment) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout._notifi_dialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        var btnClose = dialog.findViewById<Button>(R.id.dialog_oK)
        var txttitle = dialog.findViewById<MaterialTextView>(R.id.notifi_title)
        var txtcontent = dialog.findViewById<MaterialTextView>(R.id.notifi_content)
        txttitle.text = title.toString()
        txtcontent.text = content.toString()
        btnClose.setOnClickListener {
            dialog.dismiss()

//            fragmentManager.popBackStackImmediate()
            // fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            var intent =  Intent(fragment.requireActivity(),GlobitsHomeActivity::class.java)
            fragment.startActivity(intent)
            fragment.requireActivity().finish()

        }

        dialog.show()
    }
    fun createDialogHelp(context: Context,title :String?, content : String?,eventClick:()->Unit) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout._notifi_dialog_help)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        var btnClose = dialog.findViewById<Button>(R.id.btnCancel)
        var btnOk=dialog.findViewById<Button>(R.id.dialog_ok)
        var txttitle = dialog.findViewById<MaterialTextView>(R.id.notifi_title)
        var txtcontent = dialog.findViewById<MaterialTextView>(R.id.notifi_content)
        txttitle.text = title.toString()
        txtcontent.text = content.toString()
        btnOk.setOnClickListener {
            eventClick()
            dialog.dismiss()
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
//        val dialogLayout = activity.layoutInflater.inflate(R.layout.dialog_disclaimer_content, null)
//
//        AlertDialog.Builder(activity)
//            .setView(dialogLayout)
//            .setCancelable(false)
//            .setNegativeButton(R.string.cancel, null)
//            .setPositiveButton(R.string.disclaimer_positive_button) { _, _ ->
//                openUrlInChromeCustomTab(activity, null, VectorSettingsUrls.DISCLAIMER_URL)
//            }
//            .show()
    }


}
