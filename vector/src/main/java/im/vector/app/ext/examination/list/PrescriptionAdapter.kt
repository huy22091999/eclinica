package im.vector.app.ext.examination.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import im.vector.app.R
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.examination.ExaminationAction
import im.vector.app.ext.examination.ExaminationViewModel
import im.vector.app.ext.utils.clickWithThrottle
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class PrescriptionAdapter(private val list: ArrayList<Prescription>, private val viewModel: ExaminationViewModel) :
    RecyclerView.Adapter<PrescriptionAdapter.VH>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout._globits_examination_list_item, parent, false)
        return VH(itemView)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: VH, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}
