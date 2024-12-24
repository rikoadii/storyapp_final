package com.submissionandroid.storyapp.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateEmail(input: CharSequence?) {
        val parent = parent.parent
        if (input.isNullOrEmpty()) {
            setErrorOnParentLayout("Email tidak boleh kosong", parent)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            setErrorOnParentLayout("Format email tidak valid", parent)
        } else {
            setErrorOnParentLayout(null, parent)
        }
    }

    private fun setErrorOnParentLayout(error: String?, parent: Any?) {
        if (parent is TextInputLayout) {
            parent.error = error
        }
    }
}