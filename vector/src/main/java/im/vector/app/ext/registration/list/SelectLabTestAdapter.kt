package im.vector.app.ext.registration.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.visibility
import im.vector.app.R
import im.vector.app.core.utils.DebouncedClickListener
import im.vector.app.databinding.GlobitsItemSelectLabtestBinding
import im.vector.app.ext.data.model.LabTestOrderItem
import im.vector.app.ext.utils.dpToPx
import im.vector.app.ext.utils.setMargins
import im.vector.app.ext.utils.visible
import java.text.NumberFormat

class SelectLabTestAdapter constructor(private var serviceType:Int,
    private var labTestOrderItemList: List<LabTestOrderItem>,
    private val activity: FragmentActivity,
    private val context: Context

) : RecyclerView.Adapter<SelectLabTestAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH.create(serviceType,parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(labTestOrderItemList[position], activity, context )
    }

    override fun getItemCount(): Int {
        return labTestOrderItemList.count()
    }

    class VH constructor(private var serviceType:Int,
        private var binding: GlobitsItemSelectLabtestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(labTestItem: LabTestOrderItem, activity: FragmentActivity,context: Context) {
            if (labTestItem.labTest?.name == "Xét nghiệm HIV") {
                var  textnote = labTestItem.labTest.name
                var spantextnote = SpannableString(textnote)
                var fcsRed = ForegroundColorSpan(Color.RED)
                spantextnote.setSpan(fcsRed,labTestItem.labTest.name.length,textnote.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                var text = spantextnote
                binding.labtestFrameLayoutText.text = text
                binding.labtestFrameLayoutTextWarning.text=context.getString(R.string.lab_tests_important)
                binding.labtestFrameLayoutTextWarning.visible(true)
                binding.labtestFrameLayoutTextWarning.visibility=ViewGroup.VISIBLE

            } else

                binding.labtestFrameLayoutText.text = labTestItem.labTest?.name

            //TODO nếu xoá code trong if này sẽ bị bug mất id của labTest
            if(labTestItem.labTest?.id != null) {
                binding.labTestId.text = labTestItem.labTest.id
            }
            if (labTestItem.labTest != null && labTestItem.labTest.labTestListService?.size!! > 0) {
                binding.rdGroup.removeAllViews()
                for(i in 0 until (labTestItem.labTest.labTestListService.size)){
                    // Create RadioButton programmatically
                    val radioButton = RadioButton(activity)
                    radioButton.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(48,context))
                    radioButton.setTextAppearance(R.style.CommonText)
                    radioButton.buttonTintList=activity.getColorStateList(R.color.riotx_accent)
                    radioButton.background=activity.getDrawable(R.drawable.background_edit_text)
                    radioButton.setPadding(dpToPx(20,context), 0, 0,0)
                    radioButton.setMargins(0, dpToPx(12,context),0,0)

                    radioButton.text = (labTestItem.labTest.labTestListService[i].service?.name ?: "")
                    radioButton.id = i
                    if(serviceType==2){
                        binding.layoutPrice.visible(false)
                    }
                    if (labTestItem.labTestAndService?.service?.id
                            .equals(labTestItem.labTest.labTestListService[i].service?.id)) {
                        radioButton.toggle()
                        binding.price.text=labTestItem.labTest.labTestListService[i].price.toString()
                    }

                    binding.rdGroup.addView(radioButton)
                }

                binding.rdGroup.setOnCheckedChangeListener { _, i ->
                    labTestItem.labTestAndService = labTestItem.labTest.labTestListService[i]
                    binding.price.text= labTestItem.labTest.labTestListService[i].price?.let {
                        convert(
                            it
                        )
                    }
                }
            }
        }
        fun convert(price:Double):String{
            var value=NumberFormat.getInstance().format(price)
            return "$value VND"
        }
        companion object {
            fun create(serviceType:Int,parent: ViewGroup): VH {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout._globits_item_select_labtest, parent, false)
                val binding = GlobitsItemSelectLabtestBinding.bind(view)
                return VH(serviceType,binding)
            }
        }
    }
}
