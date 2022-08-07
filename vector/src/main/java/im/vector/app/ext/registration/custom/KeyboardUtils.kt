package com.example.demofragment

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class KeyboardUtils {

    var wasOpened = false
    var wasOpenedRefund = false
    private var heightDiff = 0
    private val DefaultKeyboardDP = 100

    // Lollipop includes button bar in the root. Add height of button bar (48dp) to maxDiff
    @SuppressLint("ObsoleteSdkInt")
    private val EstimatedKeyboardDP =
        DefaultKeyboardDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
    private var instance: KeyboardUtils? = null

    @Synchronized
    fun getInstance(): KeyboardUtils? {
        if (instance == null) {
            instance = KeyboardUtils()
        }
        return instance
    }

//    fun hideKeyboard(activity: Activity) {
//        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        var view = activity.currentFocus
//        if (view == null) view = View(activity)
//        if (imm == null) return
//        imm.hideSoftInputFromWindow(view.windowToken, 0)
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun hideKeyboardDialog(activity: Activity) {
        val inputMethodManager =
            (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun showKeyboard(activity: Activity) {
        val inputMethodManager =
            (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }



    private fun setKeyboardListener(activity: Activity, h: Int, scrollView: View) {
        val activityRootView =
            (activity.findViewById<View>(R.id.content) as ViewGroup).getChildAt(0)
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            private val r = Rect()
            override fun onGlobalLayout() {
                // Convert the dp to pixels.
                val estimatedKeyboardHeight = TypedValue
                    .applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        EstimatedKeyboardDP.toFloat(),
                        activityRootView.resources.displayMetrics
                    )
                    .toInt()

                // Conclude whether the keyboard is shown or not.
                activityRootView.getWindowVisibleDisplayFrame(r)
                heightDiff = activityRootView.rootView.height - (r.bottom - r.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                Log.d("sdhfskdjhfks", "isShown: $isShown")
                if (isShown == wasOpened) {
                    return
                }
                //
                wasOpened = isShown
                if (isShown) {
                    //Set listview height
                    val layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 0, 0, heightDiff - h)
                    val a = heightDiff - h
                    Log.d("sdhfskdjhfks", "onGlobalLayout: $a")
                    scrollView.layoutParams = layoutParams
                }
            }
        })
    }

    fun checkKeyBoard(activity: Activity, h: Int, scrollView: View) {
        KeyboardVisibilityEvent.setEventListener(
            activity
        ) { isOpen: Boolean ->
            if (isOpen) {
                setKeyboardListener(activity, h, scrollView)
            } else {
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                )
                scrollView.layoutParams = layoutParams
            }
        }
        setKeyboardListener(activity, h, scrollView)
    }
}
