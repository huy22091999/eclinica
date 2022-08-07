package im.vector.app.ext.registration.list

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.R
import im.vector.app.ext.data.model.LabTestOrderItem
import im.vector.app.ext.utils.getSpannedText
import io.realm.Realm.getApplicationContext
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class LabTestResultAdapter(private val list: ArrayList<LabTestOrderItem>) :
    RecyclerView.Adapter<LabTestResultAdapter.VH>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout._globits_item_labtest_result, parent, false)
        return VH(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentItem = list[position]
        holder.labTestName.text = currentItem.labTest?.name

        val formatter = SimpleDateFormat("dd/MM/yyyy")

        if (currentItem.performedDate != null) {
            val performedDateString : String = formatter.format(currentItem.performedDate)
            String.format(" %s", performedDateString)
                .also { holder.performedDate.text = it }
        }
        if (currentItem.sampleDate != null) {
            val specimenDateString : String = formatter.format(currentItem.sampleDate)
            String.format(" %s", specimenDateString)
                .also { holder.specimenDate.text = it }
        }

        if (currentItem.labTest?.resultType == 1) {
            String.format("<font color=\"red\">%s (%s)</font>", currentItem.numericResult, currentItem.labTest.unit)
                .also { holder.labTestResult.text = getSpannedText(it) }
        } else {
            String.format("<font color=\"red\">%s</font>", currentItem.stringResult)
                .also { holder.labTestResult.text = getSpannedText(it) }
        }
        if (currentItem.numericResult == null && currentItem.stringResult == null) {
            holder.labTestResult.text =
                getApplicationContext()?.getString(R.string.not_result_yet)
                    ?.let { getSpannedText(it) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labTestName : TextView = itemView.findViewById(R.id.labtest_name)
        val specimenDate : TextView = itemView.findViewById(R.id.labtest_specimen_date)
        val performedDate : TextView = itemView.findViewById(R.id.labtest_performed_date)
        val labTestResult : TextView = itemView.findViewById(R.id.labtest_result)

    }

}
