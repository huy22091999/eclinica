package im.vector.app.ext.registration.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.R
import im.vector.app.core.epoxy.LayoutManagerStateRestorer
import im.vector.app.core.utils.DebouncedClickListener
import im.vector.app.databinding.GlobitsItemSelectLabtestBinding
import im.vector.app.databinding.GlobitsItemSelectLabtestServiceBinding
import im.vector.app.ext.data.model.LabTestOrderItem
import im.vector.app.ext.data.model.LabTestService
import im.vector.app.ext.registration.RegistrationActions
import im.vector.app.ext.registration.RegistrationViewState

class SelectLabTestServiceAdapter constructor(
    private var labTestItemServiceList: List<LabTestService>,
) : RecyclerView.Adapter<SelectLabTestServiceAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH.create(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(labTestItemServiceList[position])

    }

    override fun getItemCount(): Int {
        return labTestItemServiceList.count()
    }
    class VH constructor(
        private var binding: GlobitsItemSelectLabtestServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(labTestItem: LabTestService) {
            binding.labtestServiceItem.text = labTestItem.service?.name
            binding.root.setOnClickListener(DebouncedClickListener({
                this.bindingAdapter?.notifyItemChanged(absoluteAdapterPosition)
            }))
        }
        companion object {
            fun create(parent: ViewGroup): VH {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout._globits_item_select_labtest_service, parent, false)
                val binding = GlobitsItemSelectLabtestServiceBinding.bind(view)
                return VH(binding)
            }
        }
    }
}
