package im.vector.app.ext.registration.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.R
import im.vector.app.databinding.GlobitsLayoutFileitemBinding
import im.vector.app.ext.data.model.FiledtoContent

class AttachFildeAdapter
//constructor(
//    private var content: ArrayList<FiledtoContent>
//)
    : RecyclerView.Adapter<AttachFildeAdapter.VH>() {
    private var onlyView = true
    lateinit var listener: ItemListener


    var content: ArrayList<FiledtoContent> = ArrayList()

    fun setOnItemListener(listener: ItemListener?) {
        this.listener = listener!!
    }

    fun setData(data: ArrayList<FiledtoContent>) {
        content = data
        notifyDataSetChanged()
    }

    fun setOnlyView(onlyView: Boolean?) {
        this.onlyView = onlyView!!
    }

    fun getData(): ArrayList<FiledtoContent> {
        return content
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return create(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(content.get(position))
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return if (content.isNullOrEmpty()) 0 else content.size
    }


    inner class VH constructor(private var binding: GlobitsLayoutFileitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: FiledtoContent) {
            binding.txtFilename.text = content.file!!.name
            binding.txtFiledes.text = content.description

            binding.btnDeletefile.setOnClickListener {
                listener.onItemDeleteFile(
                    absoluteAdapterPosition
                )
            }
            binding.btnDowfile.setOnClickListener { listener.onItemDowFile(absoluteAdapterPosition) }
            binding.btnEditfile.setOnClickListener { listener.onItemEditFile(absoluteAdapterPosition) }
            if (!onlyView) {
                binding.btnDeletefile.isEnabled = false
                binding.btnEditfile.isEnabled = false
            }
        }


    }

    fun create(parent: ViewGroup): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout._globits_layout_fileitem, parent, false)
        val binding = GlobitsLayoutFileitemBinding.bind(view)
        return VH(binding)
    }

    interface ItemListener {
        fun onItemClick(position: Int)
        fun onItemDeleteFile(position: Int)
        fun onItemDowFile(position: Int)
        fun onItemEditFile(position: Int)
    }
}
