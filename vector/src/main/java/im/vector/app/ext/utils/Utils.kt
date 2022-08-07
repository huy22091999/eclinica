package im.vector.app.ext.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.SystemClock
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.R
import im.vector.app.ext.custom.ExposedDropdownMenu
import java.security.AccessController.getContext



/**
 * Set transparent status bar and navigation bar
 */
fun dpToPx(dp: Int,context:Context): Int {
    val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}
fun getSpannedText(text: String): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    } else {
        TODO("VERSION.SDK_INT < N")
    }
}
fun Activity.makeBarsTransparent() {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//    if (fullscreen == true) {
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
//    }
    window.statusBarColor = android.graphics.Color.TRANSPARENT
    window.navigationBarColor = android.graphics.Color.TRANSPARENT
}

/**
 * Get action bar height
 */
fun Activity.getActionBarHeight(): Int {
    var actionBarHeight = 0
    val tv = TypedValue()
    if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    }

    return actionBarHeight
}

/**
 * Get height of the status bar
 */
fun Activity.getStatusBarHeight(): Int {
    var statusBarHeight = 0
    val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resId)
    }

    return statusBarHeight
}

/**
 * Get height of the bottom navigation bar (if available)
 */
fun Activity.getNavBarHeight(): Int {
    val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

    if (!hasMenuKey && !hasBackKey) {
        // the device has a navigation bar
        val orientation = resources.configuration.orientation
        val resId = if (isTablet()) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            } else {
                resources.getIdentifier("navigation_bar_height_landscape", "dimen", "android")
            }
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            } else {
                resources.getIdentifier("navigation_bar_width", "dimen", "android")
            }
        }

        if (resId > 0) {
            return resources.getDimensionPixelSize(resId)
        }
    }

    return 0
}

/**
 * Check whether the current device is a tablet
 */
fun Activity.isTablet(): Boolean =
    (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

/**
 * Convert DP to pixels
 */
fun Activity.dpToPixels(dp: Float) =
    dp * resources.displayMetrics.density

/**
 * Convert pixels to DP
 */
fun Activity.pixelsToDp(pixels: Float) =
    pixels / resources.displayMetrics.density

/**
 * Convert pixels to SP
 */
fun Activity.pixelsToSp(pixels: Float) =
    pixels / resources.displayMetrics.scaledDensity

/**
 * Convert SP to pixels
 */
fun Activity.spToPixels(sp: Float) =
    sp * resources.displayMetrics.scaledDensity

/**
 * Set toolbar icon tint
 */
fun Activity.setToolbarIconTint(item: MenuItem, ic: Int, color: Int) {
    val icon = ResourcesCompat.getDrawable(resources, ic, null)
    icon?.setTint(resources.getColor(color, theme))

    item.icon = icon
}

/**
 * Show snackbar
 */
fun Activity.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar =
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction(getString(R.string.retry)) {
            it()
        }
    }
    snackbar.show()
}

/**
 * Set group visibility
 */
fun Group.setGroupVisibility(layout: ConstraintLayout, visibility: Int) {
    val refIds = this.referencedIds
    for (id in refIds) {
        layout.findViewById<View>(id).visibility = visibility
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.clickWithThrottle(throttleTime: Long = 1000L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

/**
 * Adjust margin top for a view to ensure it is not hidden under the status bar
 */
fun View.adjustMarginTop(activity: Activity) {
    val statusBarHeight = activity.getStatusBarHeight()
    setMargins(marginLeft, marginTop + statusBarHeight, marginRight, marginBottom)
}

/**
 * Set margin for a view
 */
fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        requestLayout()
    }
}

/**
 * Adjust bottom app bar padding to avoid overlap
 */
fun BottomAppBar.adjustBottomNavPadding(activity: Activity) {
    if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        return
    }

    val navBarHeight = activity.getNavBarHeight()
//    layoutParams.apply {
//        this.height = height + navBarHeight
//    }
//    requestLayout()
    setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom + navBarHeight)
}

/**
 * Adjust the padding of the toolbar to avoid being hidden under the status bar
 */
fun Toolbar.adjustToolbarPadding(activity: Activity) {
    if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        return
    }

    val statusBarHeight = activity.getStatusBarHeight()
    layoutParams.apply {
        this.height = height + statusBarHeight
    }
    requestLayout()
    setPadding(this.paddingLeft, this.paddingTop + statusBarHeight, this.paddingRight, this.paddingBottom)
}



fun Fragment.validateMenu(ex: ExposedDropdownMenu, required: Boolean, text: TextInputLayout) {
    if (required && ex.text.isNullOrEmpty()) {
        getString(R.string.msg_required_field).also {
            text.error = it
        }
        ex.requestFocus()
    }

    ex.addTextChangedListener(object: TextWatcher {override fun afterTextChanged(s: Editable?) {

    }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            text.error = null
        }

    })

}


 fun Fragment.validateField(
    textField: TextInputEditText,
    required: Boolean,
    min: Int,
    text: TextInputLayout
) {
    if (required && textField.toStringAlt().isNullOrEmptyAlt()) {
        getString(R.string.msg_required_field).also {
            text.error = it

        }
        textField.requestFocus()
    }
    if (min != -1 && (!textField.toStringAlt()
            .isNullOrEmptyAlt() && textField.toStringAlt().length < min)
    ) {
        getString(R.string.msg_validate_min_field).also {
            text.error = it

        }
        textField.requestFocus()
    }
    textField.addTextChangedListener(object: TextWatcher {override fun afterTextChanged(s: Editable?) {

    }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            text.error = null
        }

    })

}

