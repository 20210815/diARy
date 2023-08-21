package com.example.diary

import android.net.Uri
import java.sql.Date
import java.sql.Time

data class DiaryPlaceModel(
    var place: String? = "여행지",
    var content: String? = null,
    var imageUris: ArrayList<Uri>? = null,
    var placeDate: String? = "", //우선 문자열로 (추후 Date, Time)
    var placeTimeS: String? = "",
    var placeTimeE: String? = ""
)