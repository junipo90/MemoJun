package com.example.memojun

import android.content.Context
import android.location.Geocoder
import android.util.AttributeSet
import kotlinx.android.synthetic.main.view_info.view.*
import java.util.*

class LocationInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):InfoView(context, attrs, defStyleAttr){
    init {
        typeImage.setImageResource(R.drawable.ic_location)
        infoText.setText("")
    }

    fun setLocation(latitude: Double, longtitude:Double){
        if (latitude == 0.0 && longtitude == 0.0){
            infoText.setText("위치정보가 없습니다")
        }else{
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude,longtitude, 1)
            infoText.setText("${addresses[0].adminArea}")
        }
    }
}