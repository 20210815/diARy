package com.example.diary

import com.google.gson.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class MyApplication {
    val retrofit = Retrofit.Builder()
        .baseUrl("http:/192.168.219.192:8080/") // 서버 임시 URL(로컬)
        .addConverterFactory(GsonConverterFactory.create(getGson()))
        .build()

    fun getGson(): Gson {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // 원하는 시간 형식 지정

        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd") // Date 형식 지정
            .registerTypeAdapter(Time::class.java, TimeSerializer(timeFormat))
            .registerTypeAdapter(Time::class.java, TimeDeserializer(timeFormat))

            //.registerTypeAdapter(Time::class.java, timeDeserializer) // 시간 변환기 등록
            .create()
    }

    val planService = retrofit.create(PlanService::class.java)
    val myPlanService = retrofit.create(MyPlanService::class.java)
    val planDetailService = retrofit.create(PlanDetailService::class.java)
    val deletePlanService = retrofit.create(DeletePlanService::class.java)
    val diaryService = retrofit.create(DiaryService::class.java)
}

class TimeSerializer(private val timeFormat: SimpleDateFormat) : JsonSerializer<Time> {
    override fun serialize(src: Time?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(timeFormat.format(src))
    }
}

class TimeDeserializer(private val timeFormat: SimpleDateFormat) : JsonDeserializer<Time> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Time {
        val timeString = json?.asString
        val time = timeFormat.parse(timeString)
        return Time(time.time)
    }
}