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

    // 위치 정보 설정 함수
    fun setLocation(latitude: Double, longtitude:Double){
        if (latitude == 0.0 && longtitude == 0.0){
            infoText.setText("위치정보가 없습니다")
        }else{
            // 위치값이 있다면 구글에서 제공하는 Geocoder로 좌표를 지역이름으로 변환하여 출력
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude,longtitude, 1)
            infoText.setText("${addresses[0].adminArea}")
        }
    }
}