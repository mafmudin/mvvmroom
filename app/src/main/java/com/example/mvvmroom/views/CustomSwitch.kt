package com.example.mvvmroom.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mvvmroom.R

class CustomSwitch @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var activeTextView : TextView
    private var inactiveTextView : TextView
    private var status = 1

    init {
        orientation = HORIZONTAL
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.switch_layout, this, true)

        activeTextView = getChildAt(0) as TextView
        inactiveTextView = getChildAt(1) as TextView

        activeTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        activeTextView.setBackgroundResource(R.color.blue)


        activeTextView.setOnClickListener {
            status = 1
            switch(context, activeTextView, inactiveTextView)
        }

        inactiveTextView.setOnClickListener {
            status = 2
            switch(context, inactiveTextView, activeTextView)
        }
    }

    fun switch(context: Context, selected: TextView, textView: TextView){
        selected.setBackgroundResource(R.color.blue)
        textView.setBackgroundResource(R.color.gray)

        selected.setTextColor(ContextCompat.getColor(context, R.color.white))
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
    }

    fun getStatus(): Int{
        return status
    }

    fun setStatus(status: Int){
        if (status == 1){
            activeTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
            activeTextView.setBackgroundResource(R.color.blue)


            inactiveTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            inactiveTextView.setBackgroundResource(R.color.gray)
        }else{
            inactiveTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
            inactiveTextView.setBackgroundResource(R.color.blue)


            activeTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            activeTextView.setBackgroundResource(R.color.gray)
        }
    }
}
