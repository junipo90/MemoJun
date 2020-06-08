package com.example.memojun

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import java.util.jar.Attributes

open class InfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.view_info, this)
    }
}