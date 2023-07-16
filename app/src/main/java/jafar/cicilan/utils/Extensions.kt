package jafar.cicilan.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.icu.text.NumberFormat.getCurrencyInstance
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import jafar.cicilan.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/* Get date for locale */
val currentDate = Calendar.getInstance().timeInMillis

/* Format Rupiah Indonesia */
fun rupiahFormat(number: Int?): String =
    getCurrencyInstance(Locale("in", "ID")).format(number)

/* Convert date and time to locale */
fun Long.format(pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT)).format(Date(this))

/* Show soft keyboard for EditText */
fun TextInputEditText.showSoftKeyboard() {
    if (this.requestFocus()) (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

///* Hide Soft Keyboard */
//fun TextInputEditText.hideSoftKeyboard() {
//    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
//        .hideSoftInputFromWindow(this.windowToken, 0)
//}

/* EditText changed event observer */
fun TextInputEditText.afterInputNumberChanged(afterTextChanged: (Int) -> Unit) =
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.getNumber())
        }
    })

fun TextInputEditText.afterInputStringChanged(afterTextChanged: (String?) -> Unit) =
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })

fun MaterialAutoCompleteTextView.afterInputStringChanged(afterTextChanged: (String?) -> Unit) =
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })

/* Convert money format of rupiah */
fun Editable?.getNumber(): Int = this?.filter { it.isDigit() }?.toString()?.toIntOrNull() ?: 0
fun TextInputEditText.addAutoConverterToMoneyFormat(layout: TextInputLayout) =
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.filter { it.isDigit() }?.toString()?.let {
                removeTextChangedListener(this)
                if (it.isNotEmpty()) {
                    val result = it.toIntOrNull()
                    if (result != null) {
                        val format = NumberFormat.getInstance(Locale("in")).format(result)
                        setText(format)
                        setSelection(format.length)
                    } else layout.error = context.getString(R.string.input_over)

                } else text?.clear()

                addTextChangedListener(this)
                layout.error = null
            }
        }
    })

/* Toast SHOW UP */
fun Fragment.showToast(message: String?) =
    Toast.makeText(requireContext(), message ?: "Unknown message", Toast.LENGTH_SHORT).show()

/* Running in background ViewModel */
fun ViewModel.runInBackground(action: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch(Dispatchers.IO) { action() }

/* Running in background Fragment */
fun Fragment.runWhenCreated(action: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.CREATED) { action() }
}

fun Fragment.runWhenStarted(action: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) { action() }
}

fun Fragment.runWhenResumed(action: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.RESUMED) { action() }
}

fun AppCompatActivity.runWhenCreated(action: suspend CoroutineScope.() -> Unit) =
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.CREATED) { action() }
    }

fun Int.dotPixel() = (this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()

/* Image to .PNG */
fun formatPNG(): Bitmap.CompressFormat {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return Bitmap.CompressFormat.WEBP_LOSSLESS
    }
    return Bitmap.CompressFormat.PNG
}
