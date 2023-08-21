package com.example.diary

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication {
    val retrofit = Retrofit.Builder()
        .baseUrl("http:/192.168.219.160:8080/") // 서버 임시 URL(로컬)
        .addConverterFactory(GsonConverterFactory.create(getGson()))
        .build()

    fun getGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd") // Date 형식 지정
            .create()
    }

    val planService = retrofit.create(PlanService::class.java)
    val myPlanService = retrofit.create(MyPlanService::class.java)
    val planDetailService = retrofit.create(PlanDetailService::class.java)
    val deletePlanService = retrofit.create(DeletePlanService::class.java)
    val diaryService = retrofit.create(DiaryService::class.java)
}