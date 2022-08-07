package im.vector.app.ext.custom

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.forEach
import com.google.android.material.tabs.TabLayout

class ScrollableTabLayout : TabLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val tabLayout = getChildAt(0) as ViewGroup
        val childCount = tabLayout.childCount
        if (childCount > 0) {
            val widthPixels = MeasureSpec.getSize(widthMeasureSpec)
            val tabMinWidth = widthPixels / childCount
            var remainderPixels = widthPixels % childCount
            tabLayout.forEach {
                if (remainderPixels > 0) {
                    it.minimumWidth = tabMinWidth + 1
                    remainderPixels--
                } else {
                    it.minimumWidth = tabMinWidth
                }
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
