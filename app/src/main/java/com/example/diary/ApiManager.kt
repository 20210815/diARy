package com.example.diary

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlanManager {
    fun sendPlanToServer(planData: PlanData) {
        val apiService = MyApplication().planService
        val call = apiService.sendPlan(planData)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("서버 테스트", "성공")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("서버 테스트1", "오류: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("서버 테스트2", "오류: ${t.message}")
            }
        })
    }
}

object MyPlanListManager {
    fun getPlanListData(onSuccess: (List<MyPlanListResponse>) -> Unit, onError: (Throwable) -> Unit) {
        val apiService = MyApplication().myPlanService
        val call = apiService.getPlanData()

        call.enqueue(object : Callback<List<MyPlanListResponse>> {
            override fun onResponse(call: Call<List<MyPlanListResponse>>, response: Response<List<MyPlanListResponse>>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        onSuccess(it)
                    } ?: run {
                        onError(Throwable("Response body is null"))
                    }
                } else {
                    onError(Throwable("API call failed with response code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<MyPlanListResponse>>, t: Throwable) {
                onError(t)
            }
        })
    }
}

object PlanDetailManager {
    fun getPlanDetailData(planId: Int, onSuccess: (PlanDetailResponse) -> Unit, onError: (Throwable) -> Unit) {
        val apiService = MyApplication().planDetailService
        val call = apiService.getPlanData(planId)

        call.enqueue(object : Callback<PlanDetailResponse> {
            override fun onResponse(call: Call<PlanDetailResponse>, response: Response<PlanDetailResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        onSuccess(it)
                    } ?: run {
                        onError(Throwable("Response body is null"))
                    }
                } else {
                    onError(Throwable("API call failed with response code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<PlanDetailResponse>, t: Throwable) {
                onError(t)
            }
        })
    }
}

object DeletePlanManager {
    fun deleteDataFromServer(planId: Int) {
        val apiService = MyApplication().deletePlanService
        val call = apiService.deletePlanData(planId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("서버 테스트", "삭제 성공")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("서버 테스트1", "오류: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("서버 테스트2", "오류: ${t.message}")
            }
        })
    }
}

object DiaryManager {

    fun sendDiaryToServer(diaryData: DiaryData) {
        val apiService = MyApplication().diaryService
        val call = apiService.sendDiary(diaryData)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("서버 테스트", "성공")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("서버 테스트1", "오류: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("서버 테스트2", "오류: ${t.message}")
            }
        })
    }
}