package im.vector.app.ext.registration.list

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import im.vector.app.R
import im.vector.app.ext.data.model.FiledtoContent
import im.vector.app.ext.data.model.LabTestOrderAttachment
import im.vector.app.ext.data.model.LabTestOrderItem
import java.lang.Exception

class LabTestResultFileAdapter() :
    RecyclerView.Adapter<LabTestResultFileAdapter.VH>() {

    lateinit var listener: LabItemListener

    private var list: ArrayList<LabTestOrderAttachment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout._globits_labtest_file, parent, false)
        return VH(itemView)
    }

    fun getData() : ArrayList<LabTestOrderAttachment> =   list
    fun setData(data :ArrayList<LabTestOrderAttachment> ){
        list = data
        notifyDataSetChanged()
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, @SuppressLint("RecyclerView") position: Int) {


        val currentItem = list[position]

        holder.fileDescription.setText(currentItem.description ?: "" )
        holder.filename.text = String.format("Tên file: %s",if(currentItem.file?.name !=null) currentItem.file!!.name else "")

        holder.labItemDelete.setOnClickListener {
            listener.onLabItemDeleteFile(position)
        }

        holder.labItemDow.setOnClickListener {
            listener.onLabItemDowFile(position)
        }

        holder.labItemDetail.setOnClickListener {
            listener.onLabItemDetailFile(position)
        }

//        holder.labItemEdit.setOnClickListener {
//            listener.onLabItemEditFile(position)
//        }


        holder.fileDescription.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                list.get(position).description = holder.fileDescription.text.toString()
            }
        })




    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filename: TextView = itemView.findViewById(R.id.labtest_file_name)
        val fileDescription: TextInputEditText = itemView.findViewById(R.id.labtest_file_description)


        var labItemDelete: AppCompatImageButton = itemView.findViewById(R.id.delete_file_labtest)
        var labItemDow: AppCompatImageButton = itemView.findViewById(R.id.dow_file_labtest)
        var labItemDetail: AppCompatImageButton = itemView.findViewById(R.id.watch_file_labtest)

    }

    interface LabItemListener {
        fun onLabItemDeleteFile(position: Int)
        fun onLabItemDowFile(position: Int)
        fun onLabItemDetailFile(position: Int)
//        fun onLabItemEditFile(position: Int)


    }
}
