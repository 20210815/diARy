package com.example.diary

import java.sql.Date
import java.sql.Time

data class DiaryDto(
    val title: String,
    val satisfaction: Int,
    val public: Boolean,
    val travelStart: Date,
    val travelEnd: Date,
    //val user: UserId,
    val travelDest: String,
    val memo: String,
    val tags: List<TagName>
)

data class UserId(
    val userId: Int
)

data class TagName(
    val name: String
)

data class DiaryLocationDto(
    val content: String,
    val name: String,
    val address: String,
    val date: Date,
    val timeStart: Time,
    val timeEnd: Time,
    val diaryLocationImageDtoList: List<DiaryLocationImageDto>
)

data class DiaryLocationImageDto(
    val imageData: String //수정 필요
)

// 일기 작성
data class DiaryData(
    val diaryDto: DiaryDto,
    val diaryLocationDto: List<DiaryLocationDto>
)

// 유저별 일기 목록
data class MyDiary(
    val diaryId: Int,
    val travelDest: String,
    val title: String,
    val travelStart: String, //임시 스트링
    val travelEnd: String,
    val diaryLike: Int,
    val comment: Int,
    val createdAt: String,
    val updatedAt: String,
    val public: Boolean
)


