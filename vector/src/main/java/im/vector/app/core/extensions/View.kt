/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.core.extensions

import android.app.Activity
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.material.textfield.TextInputEditText
import im.vector.app.ext.custom.ExposedDropdownMenu

fun View.hideKeyboard(activity: Activity? = null) {
    val imm = context?.getSystemService<InputMethodManager>()
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    imm?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}
fun ExposedDropdownMenu.hideKeyboardDrop(activity: Activity?=null){
    inputType = InputType.TYPE_NULL
    imeOptions = EditorInfo.IME_ACTION_DONE
    showSoftInputOnFocus = false
    onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
        hideKeyboard(activity)
    }
    onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
        hideKeyboard(activity)
    }
}
fun View.showKeyboard(andRequestFocus: Boolean = false) {
    if (andRequestFocus) {
        requestFocus()
    }
    val imm = context?.getSystemService<InputMethodManager>()
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
fun TextInputEditText.onlyNumber():Boolean
{
    for (item in text.toString() )
    {
        if(!Character.isDigit(item))
            return false
    }
    return true
}

