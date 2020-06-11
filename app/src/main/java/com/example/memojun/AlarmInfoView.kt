package com.example.memojun

import android.content.Context
import android.util.AttributeSet
import kotlinx.android.synthetic.main.view_info.view.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):InfoView(context, attrs,defStyleAttr){ // InfoView 를 상속 받아 값을 넘겨 줌
    companion object{ // 클래스 공용부인 companion object 안에 알람의 시간 표시 형식을 만들어 줌
        private val dateFormat = SimpleDateFormat("yy/MM/dd HH:mm")
    }

    // 클래스의 초기화 부분 init 에서 View 에 표시할 초기값을 지정
    init {
        typeImage.setImageResource(R.drawable.ic_alarm)
        infoText.setText("")
    }

    // 알람 시간을 입력받아 이전시간이면 알람 없음, 이후면 알람 시간 표시
    fun setAlarmDate(alarmDate : Date){
        if (alarmDate.before(Date())){
            infoText.setText("알람이 없습니다")
        }else{
            infoText.setText(dateFormat.format(alarmDate))
        }
    }
}