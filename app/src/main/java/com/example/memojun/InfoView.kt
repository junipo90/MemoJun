package com.example.memojun

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import java.util.jar.Attributes
// 안드로이 시스템에서 View 를 생성할 때 Java 생성자 형태로 호출되기 때문에 default argument 를 호환되도록 만들어 줌
open class InfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) { // LinearLayout 을 상속 받아 생성자의 값들을 넘겨 줌
    init { // init 내에서 View 의 inflate 함수를 사용하여 view_info.xml 을 내부에 포함시킴
        inflate(context, R.layout.view_info, this)
    }
}