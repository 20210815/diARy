package com.example.diary

import android.net.Uri
import java.sql.Date
import java.sql.Time

data class PlanDetailModel( //일정 안 여행지 카드
    var place: String? = "여행지",
    var address: String? = null,
    var tel: String? = null,
    var imageUris: Uri? = null,
    var placeDate: Date,
    var placeStart: String? = null, //임시로 스트링 지정
    var placeEnd: String? = null
)
