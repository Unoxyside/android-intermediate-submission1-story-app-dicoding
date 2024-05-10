package com.bahasyim.mystoryapp.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.bahasyim.mystoryapp.R

class CustomEditTextEmail: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().contains("@") && s.toString().contains(".")){
                    error = null
                } else {
                    setError(context.getString(R.string.email_invalid_messege), null)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }
}