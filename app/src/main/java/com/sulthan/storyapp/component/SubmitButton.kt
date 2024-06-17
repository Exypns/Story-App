package com.sulthan.storyapp.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.sulthan.storyapp.R

class SubmitButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    private var txtColor : Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable

    init {
        txtColor = ContextCompat.getColor(context, android.R.color.white)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_submit_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_submit_button_disable) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(txtColor)
        textSize = 12F
        gravity = Gravity.CENTER
    }
}