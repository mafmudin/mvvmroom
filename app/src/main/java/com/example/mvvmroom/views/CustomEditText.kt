package com.example.mvvmroom.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mvvmroom.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var textView : TextView
    private var editText: EditText
    private var fieldName : String = ""
    init {
        orientation = VERTICAL
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_edit_text, this, true)

        val a = context.obtainStyledAttributes(attrs, R.styleable.custom_edit_text, 0, 0)

        try {
            fieldName = a.getString(R.styleable.custom_edit_text_field_name)!!
        }finally {
            a.recycle()
        }

        textView = getChildAt(0) as TextView
        editText = getChildAt(1) as EditText
        editText.setBackgroundResource(R.drawable.edit_text_state)


        textView.text = fieldName
    }

    fun setFieldName(fieldName: String){
        textView.text = fieldName
    }

    fun setText(text: String){
        editText.setText(text)
    }

    fun getText(): String{
        return editText.text.toString()
    }

}
