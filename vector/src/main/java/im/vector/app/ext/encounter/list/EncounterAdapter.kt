package im.vector.app.ext.encounter.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import im.vector.app.R
import im.vector.app.ext.encounter.EncounterViewModel
import im.vector.app.ext.data.model.Encounter
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.encounter.EncounterActions
import im.vector.app.ext.utils.clickWithThrottle
import im.vector.app.ext.utils.visible
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class EncounterAdapter(private val list: ArrayList<Encounter>, private val viewModel: EncounterViewModel) :
    RecyclerView.Adapter<EncounterAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout._globits_encounter_list_item, parent, false)
        return VH(itemView)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentItem = list[position]
        holder.encounterIndex.text = String.format("Lần khám: %s", currentItem.indexNumber.toString())

        holder.btnDetail.clickWithThrottle {
            holder.detail.visible(true)
            holder.btnDetail.visible(false)
        }
        holder.btnHidden.clickWithThrottle {
            holder.detail.visible(false)
            holder.btnDetail.visible(true)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VH (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val encounterIndex: TextView = itemView.findViewById(R.id.encounter_index)
        val detail:LinearLayout=itemView.findViewById(R.id.layout_detail)
        val btnHidden:MaterialTextView=itemView.findViewById(R.id.btn_hidden_detail)
        val btnDetail: MaterialTextView = itemView.findViewById(R.id.btn_view_detail)
    }
}
