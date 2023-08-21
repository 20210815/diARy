package com.example.diary

import androidx.lifecycle.ViewModel

class AddPlanViewModel : ViewModel() { //일정 작성 중, 일정 내용 저장
    var enteredTitle: String? = null // 일정 작성 중, 일정 제목을 담는 변수
    var enteredSubTitle: String? = null
    var enteredStart: String? = null
    var enteredEnd: String? = null
    var enteredHash: String? = null
    var enteredClosed: Boolean = false
}