package com.example.memojun.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader
import java.net.URL

class WeatherData {
    // 객체의 생성없이 호출할 수 있도록 companion object로 생성
    companion object {
        // xml을 파싱하기위해 XmlPullParser를 생성하는 xmlPullParserFactory를 전역으로 생성
        private val xmlPullParserFactory by lazy { XmlPullParserFactory.newInstance() }

        // 위치정보를 받아 현재 날씨를 문자열로 반환하는 함수
        // suspend 를 붙여 코루틴에서 언제든지 중지 또는 재개 가능
        suspend fun getCurrentWeather(latitude: Double, longitude: Double): String {
            // 함수 내용이 네트워크로 동작하므로 코루틴을 사용해 IO쓰레드에서 동작하도록함 (그렇지 않으면 Exception 발생)
            return GlobalScope.async(Dispatchers.IO) {
                val requestUrl = "https://api.openweathermap.org/data/2.5/weather" +
                        "?lat=${latitude}&lon=${longitude}&mode=xml&units=metric&" +
                        "&appid=bc8eee49414688d0115b5c09964795df"

                var currentWeather = ""

                try {
                    // 요청할 주소 문자열을 URL 객체를 만듬
                    val url = URL(requestUrl)

                    // URL에 데이터를 요청하고 결과를 가져오는 입력 stream을 열어줌
                    val stream = url.openStream()

                    // 결과를 파싱할 수 있는 XmlPullParser를 Dactory 에서 가져옴
                    val parser = xmlPullParserFactory.newPullParser()

                    // URL의 입력 스트림을 UTF-8 인코딩 방식으로 읽을 수 있는 InputStreamReader 객체에 넣어 parser에 입력
                    parser.setInput(InputStreamReader(stream, "UTF-8"))

                    // parser의 eventType 은 문서의 시작과 끝, 태그의 시작과 끝, 태그 내의 텍스트 등을 분류해서 알려줌
                    // XmlPullParser는 문서의 시작부터 순서대로 이벤트를 파싱함
                    var eventType = parser.eventType
                    // 결과 xml에 담긴 날씨 코드를 담을 변수
                    var currentWeatherCode = 0

                    // XmlPullParser 를 이용해 문서가 끝날때까지 반복
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        // 날씨 정보가 담긴 weather 태그가 시작하는 곳을 찾음
                        if(eventType == XmlPullParser.START_TAG && parser.name == "weather") {
                            // weather 태그의 number속성을 currentWeatherCode에 가져온 후 반복문 중지
                            currentWeatherCode =
                                parser.getAttributeValue(null, "number").toInt()
                            break
                        }
                        // 반복할 때마다 next()함수를 호출해 다음 event를 파싱하고 eventType을 반환받음
                        eventType = parser.next()
                    }


                    // OpenWeather 사이트에서 제공하는 날씨 코드를 앱에서 출력할 날씨 정보 문자열로 만들어 저장
                    when (currentWeatherCode) {
                        in 200..299 -> currentWeather = "뇌우"
                        in 300..399 -> currentWeather = "이슬비"
                        in 500..599 -> currentWeather = "비"
                        in 600..699 -> currentWeather = "눈"
                        in 700..761 -> currentWeather = "안개"
                        771 -> currentWeather = "돌풍"
                        781 -> currentWeather = "토네이도"
                        800 -> currentWeather = "맑음"
                        in 801..802 -> currentWeather = "구름조금"
                        in 803..804 -> currentWeather = "구름많음"
                        else -> currentWeather = ""
                    }
                } catch (e: Exception) {
                    println(e)
                }

                currentWeather
            }.await()   // return 결과를 await() 함수로 기다렸다가 반환
        }
    }
}