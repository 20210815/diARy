package com.example.diary

import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diary.databinding.ActivityAddDiaryBinding
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class AddDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDiaryBinding
    private lateinit var viewModel: AddDiaryViewModel

    // 여행지 데이터를 저장할 리스트
    private val diaryPlaceList = mutableListOf<DiaryPlaceModel>()

    private val diaryPlaceAdapter = DiaryPlaceAdapter(diaryPlaceList)

    companion object {
        lateinit var addPlaceActivityResult: ActivityResultLauncher<Intent>
        lateinit var addContentActivityResult: ActivityResultLauncher<Intent>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.diaryAddPlaceRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddDiaryActivity)
            adapter = diaryPlaceAdapter
        }

        viewModel = ViewModelProvider(this).get(AddDiaryViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        // AddPlaceInDiaryActivity를 시작하기 위한 요청 코드 정의
        addContentActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val position = data?.getIntExtra("itemPosition", -1)
                val enteredText = data?.getStringExtra("enteredText")
                val place = data?.getStringExtra("place")
                val placeDate = data?.getStringExtra("date")
                val placeTimeS = data?.getStringExtra("timeStart")
                val placeTimeE = data?.getStringExtra("timeEnd")
                val imageUris = data?.getParcelableArrayListExtra<Uri>("imageUris")
                Log.d("리사이클러뷰", ""+position)

                if (position != null && position >= 0) {
                    val item = diaryPlaceList[position]
                    item.content = enteredText
                    item.imageUris = imageUris
                    item.place = place
                    item.placeDate = placeDate
                    item.placeTimeS = placeTimeS
                    item.placeTimeE = placeTimeE
                    diaryPlaceAdapter.notifyItemChanged(position)
                } else {
                    if (!enteredText.isNullOrEmpty() || imageUris != null) {
                        // DiaryPlaceModel 인스턴스를 생성하고 리스트에 추가
                        val newDiaryPlaceModel =
                            DiaryPlaceModel(content = enteredText, imageUris = imageUris, place = place,
                            placeDate = placeDate, placeTimeS = placeTimeS, placeTimeE = placeTimeE)
                        diaryPlaceList.add(newDiaryPlaceModel)

                        // 특정 아이템을 리스트의 맨 마지막으로 이동시키는 함수 호출
                        diaryPlaceAdapter.moveMemoItemToLast()

                        // 어댑터에 데이터 변경을 알림
                        diaryPlaceAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        // AddDiaryMapActivity를 시작하기 위한 요청 코드 정의
        addPlaceActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val position = data?.getIntExtra("itemPosition", -1)
                val enteredPlace = data?.getStringExtra("enteredPlace")
                val enteredDate = data?.getStringExtra("enteredDate")
                val enteredTimeS = data?.getStringExtra("enteredStart")
                val enteredTimeE = data?.getStringExtra("enteredEnd")

                Log.d("지도 이후 AddDiary에서 추가", ""+position + enteredPlace + enteredDate + enteredTimeS + enteredTimeE)

                if (position != null && position >= 0) {
                    val item = diaryPlaceList[position]
                    item.place = enteredPlace
                    item.placeDate = enteredDate
                    item.placeTimeS = enteredTimeS
                    item.placeTimeE = enteredTimeE
                    diaryPlaceAdapter.notifyItemChanged(position)
                } else {
                    if (!enteredPlace.isNullOrEmpty()) {
                        // DiaryPlaceModel 인스턴스를 생성하고 리스트에 추가
                        val newDiaryPlaceModel =
                            DiaryPlaceModel(place = enteredPlace,
                                placeDate = enteredDate, placeTimeS = enteredTimeS, placeTimeE = enteredTimeE)
                        diaryPlaceList.add(newDiaryPlaceModel)

                        // 특정 아이템을 리스트의 맨 마지막으로 이동시키는 함수 호출
                        diaryPlaceAdapter.moveMemoItemToLast()

                        // 어댑터에 데이터 변경을 알림
                        diaryPlaceAdapter.notifyDataSetChanged()
                    }
                }

//                val intent = Intent(this, AddPlaceInDiaryActivity::class.java)
//                intent.putExtra("itemPosition", position) // position 전달
//                intent.putExtra("place", enteredPlace)
//                intent.putExtra("date", enteredDate)
//                intent.putExtra("timeStart", enteredTimeS)
//                intent.putExtra("timeEnd", enteredTimeE)
//
//                addContentActivityResult.launch(intent)

//                if (isActivityOpen(AddPlaceInDiaryActivity::class.java)) {
//                    // 이미 Activity가 열려 있는 경우 해당 활동으로 이동
//                    val intent = Intent(this, AddPlaceInDiaryActivity::class.java)
//                    intent.putExtra("itemPosition", position) // position 전달
//                    intent.putExtra("place", enteredPlace)
//                    addContentActivityResult.launch(intent)
//                } else {
//                    // AddDiaryMapActivity가 열려 있지 않은 경우 새로운 활동 시작
//                    addContentActivityResult.launch(Intent(this, AddPlaceInDiaryActivity::class.java))
//                }
            }
        }

        // "MEMO" 항목 추가
        val initialMemo = DiaryPlaceModel(place = "MEMO", content = "클릭하여 메모를 작성하세요.")
        diaryPlaceList.add(initialMemo)
        diaryPlaceAdapter.notifyDataSetChanged()

        // 툴바 취소 버튼 클릭 시
        binding.diaryCancelBtn.setOnClickListener {
            finish()
        }

        // 툴바 완료 버튼 클릭 시
        binding.diarySaveBtn.setOnClickListener {
            // 일기 저장 처리
            saveDiaryToServer()
            finish()
        }

        binding.diaryAddStart.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, object: DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year:Int, month: Int, dayOfMonth: Int) {
                    binding.diaryAddStart.text = "${year}-${month+1}-${dayOfMonth}"
                }
            }, 2023, 9, 1)
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
        }

        binding.diaryAddEnd.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, object: DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year:Int, month: Int, dayOfMonth: Int) {
                    binding.diaryAddEnd.text = "${year}-${month+1}-${dayOfMonth}"
                }
            }, 2023, 9, 1)
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
        }

        // 여행지 추가 버튼 클릭 시
        binding.diaryAddPlaceBtn.setOnClickListener {

            // 기존의 입력을 ViewModel에 저장
            viewModel.enteredTitle = binding.diaryAddTitle.text.toString()
            viewModel.enteredDest = binding.diaryAddDest.text.toString()
            viewModel.enteredStart = binding.diaryAddStart.text.toString()
            viewModel.enteredEnd = binding.diaryAddEnd.text.toString()
            viewModel.enteredHash = binding.diaryAddHash.text.toString()
            viewModel.enteredClosed = binding.diaryAddLockBtn.isChecked

            //val intent = Intent(this, AddPlaceInDiaryActivity::class.java)
            val intent = Intent(this, AddDiaryMapActivity::class.java)
            intent.putExtra("itemPosition", -1)
            addPlaceActivityResult.launch(intent)
            //startActivity(intent)
            //startActivityForResult(intent, ADD_PLACE_REQUEST_CODE)
            //addPlaceActivityResult.launch(intent)


        }

        //메모 추가 버튼 클릭 시
        binding.diaryAddMemoBtn.setOnClickListener {

        }

    }

    override fun onResume() {
        super.onResume()

        // 이전에 입력한 텍스트를 복원하여 보여줌
        binding.diaryAddTitle.setText(viewModel.enteredTitle)
        binding.diaryAddDest.setText(viewModel.enteredDest)

        if (viewModel.enteredStart != null || viewModel.enteredEnd != null) {
            binding.diaryAddStart.setText(viewModel.enteredStart)
            binding.diaryAddEnd.setText(viewModel.enteredEnd)
        }

        if (viewModel.enteredHash != null) {
            binding.diaryAddHash.setText(viewModel.enteredHash)
        }

        binding.diaryAddLockBtn.isChecked = viewModel.enteredClosed
    }

    // 이미 열려 있는 활동 확인하는 함수
    private fun isActivityOpen(activityClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.runningAppProcesses
        if (runningActivities != null) {
            for (processInfo in runningActivities) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    try {
                        val activityName = processInfo.pkgList[0]
                        if (activityName == activityClass.canonicalName) {
                            return true
                        }
                    } catch (e: Exception) {
                        // 예외 처리
                    }
                }
            }
        }
        return false
    }

    private fun saveDiaryToServer() {
        val travelDest = binding.diaryAddDest.text.toString()
        val content = binding.diaryAddTitle.text.toString()
        val public = !binding.diaryAddLockBtn.isChecked
        val hashTagArray = binding.diaryAddHash.getInsertTag() ?: emptyArray()
        val tags: List<TagName> = hashTagArray.map { TagName(it) }
        val travelStart = binding.diaryAddStart.text.toString()
        val travelEnd = binding.diaryAddEnd.text.toString()

        val satisfaction = 4 // 만족도, 수정 필요
        val userId = 1 //작성자 유저아이디, 수정 필요
        val memo = "메모" //메모, 수정 필요

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val travelStartDate: Date = try {
            java.sql.Date(dateFormat.parse(travelStart).time)
        } catch (e: Exception) {
            java.sql.Date(System.currentTimeMillis())
        }

        val travelEndDate: Date = try {
            java.sql.Date(dateFormat.parse(travelEnd).time)
        } catch (e: Exception) {
            java.sql.Date(System.currentTimeMillis())
        }

        val diaryDto = DiaryDto(
            content, satisfaction, public, travelStartDate, travelEndDate, travelDest, memo, tags
        )

        val diaryLocations = mutableListOf<DiaryLocationDto>()

        for (item in diaryPlaceList) {
            val place = item.place ?: "여행지"
            val content = item.content ?: "일기"
            val imageUris = item.imageUris

            val placeDate: Date = try {
                java.sql.Date(dateFormat.parse(item.placeDate).time)
            } catch (e: Exception) {
                java.sql.Date(System.currentTimeMillis())
            }

            val placeTimeStart: Time = try {
                java.sql.Time(timeFormat.parse(item.placeTimeS).time)
            } catch (e: Exception) {
                java.sql.Time(System.currentTimeMillis())
            }

            val placeTimeEnd: Time = try {
                java.sql.Time(timeFormat.parse(item.placeTimeE).time)
            } catch (e: Exception) {
                java.sql.Time(System.currentTimeMillis())
            }

            if (!place.isNullOrEmpty()) {
                val diaryLocation = DiaryLocationDto(
                    content = content,
                    name = "여행지",
                    address = "", // 주소 추가 필요
                    date = placeDate,  //여행지 날짜 추가 필요
                    timeStart = placeTimeStart, // 시작 시간 추가 필요
                    timeEnd = placeTimeEnd, // 종료 시간 추가 필요
                    diaryLocationImageDtoList = listOf() // 이미지 리스트 추가 필요
                )
                diaryLocations.add(diaryLocation)
            }
        }

        val diaryLocationDto = diaryLocations
        val diaryData = DiaryData(diaryDto,diaryLocationDto)

        Log.d("서버 테스트", ""+diaryData)
        DiaryManager.sendDiaryToServer(diaryData)
    }


}