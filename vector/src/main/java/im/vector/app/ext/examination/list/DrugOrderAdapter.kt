package im.vector.app.ext.examination.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.R
import im.vector.app.ext.data.model.DrugOrder
import kotlin.collections.ArrayList

class DrugOrderAdapter(private val list: ArrayList<DrugOrder>) :
    RecyclerView.Adapter<DrugOrderAdapter.VH>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout._globits_drug_list_item, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentItem = list[position]

        holder.drugName.text = String.format("Tên thuốc: %s", currentItem.drug?.name)
        holder.drugCode.text = String.format("Mã thuốc: %s", currentItem.drug?.code)
        holder.drugManufacturer.text = String.format("Nhà sản xuất: %s", currentItem.drug?.manufacturer)
        holder.drugNumberDays.text = if (currentItem.days != null) String.format("Số ngày dùng thuốc: %s", currentItem.days.toString()) else ""
        holder.drugTotalQuantity.text = if (currentItem.totalQuantity != null) String.format("Số lượng: %s", currentItem.totalQuantity.toString()) else ""
        holder.drugNumberPerDay.text = if (currentItem.perDay != null) String.format("Liều dùng/Ngày: %s", currentItem.perDay.toString()) else ""
        holder.drugNote.text = String.format("Hướng dẫn sử dụng: %s", currentItem.note)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drugName : TextView = itemView.findViewById(R.id.drug_name)
        val drugCode : TextView = itemView.findViewById(R.id.drug_code)
        val drugManufacturer : TextView = itemView.findViewById(R.id.drug_manufacturer)
        val drugNumberDays : TextView = itemView.findViewById(R.id.drug_number_days)
        val drugTotalQuantity : TextView = itemView.findViewById(R.id.drug_total_quantity)
        val drugNumberPerDay : TextView = itemView.findViewById(R.id.drug_number_per_day)
        val drugNote : TextView = itemView.findViewById(R.id.drug_note)

    }

}
