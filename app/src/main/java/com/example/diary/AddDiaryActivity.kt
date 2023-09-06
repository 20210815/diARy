package com.example.diary

import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
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
    private var authToken: String ?= "" // 로그인 토큰
    private var new: Int ?= 1 // 새로 작성이면 1, 수정이면 0
    private var diaryId: Int ?= -1 // 일정 수정일 때의 해당 플랜 아이디

    // 여행지 데이터를 저장할 리스트
    private val diaryPlaceList = mutableListOf<DiaryPlaceModel>()

    private val diaryPlaceAdapter = DiaryPlaceAdapter(diaryPlaceList)

    private val diaryPlaceList1 = mutableListOf<DiaryDetailModel>()
    private val diaryDetailAdapter = DiaryDetailAdapter(diaryPlaceList1)

    companion object {
        lateinit var addPlaceActivityResult: ActivityResultLauncher<Intent>
        lateinit var addContentActivityResult: ActivityResultLauncher<Intent>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 저장된 토큰 읽어오기
        val sharedPreferences = getSharedPreferences("my_token", Context.MODE_PRIVATE)
        authToken = sharedPreferences.getString("auth_token", null)
        val userId = sharedPreferences.getInt("userId", -1)

        // 새로 작성 or 수정 (1이면 새로 작성, 아니면 수정)
        new = intent.getIntExtra("new_diary", 1)

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
                val placeAddress = data?.getStringExtra("address")
                //val x = data?.getStringExtra("enteredX")
                //val y = data?.getStringExtra("enteredY")
                val imageUris = data?.getParcelableArrayListExtra<Uri>("imageUris")
                Log.d("리사이클러뷰", ""+position + enteredText + place + placeDate + placeTimeS + placeTimeE)

                if (position != null && position >= 0) {
                    val item = diaryPlaceList[position]
                    item.content = enteredText
                    item.imageUris = imageUris
                    item.place = place
                    item.placeDate = placeDate
                    item.placeTimeS = placeTimeS
                    item.placeTimeE = placeTimeE
                    item.address = placeAddress
                    //item.x = x
                    //item.y = y
                    diaryPlaceAdapter.notifyItemChanged(position)
                } else {
                    if (!enteredText.isNullOrEmpty() || imageUris != null) {
                        // DiaryPlaceModel 인스턴스를 생성하고 리스트에 추가
                        val newDiaryPlaceModel =
                            DiaryPlaceModel(content = enteredText, imageUris = imageUris, place = place,
                            placeDate = placeDate, placeTimeS = placeTimeS, placeTimeE = placeTimeE, address = placeAddress)
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
                val enteredAddress = data?.getStringExtra("enteredAddress")
                val enteredX = data?.getStringExtra("enteredX")
                val enteredY = data?.getStringExtra("enteredY")

                Log.d("지도 이후 AddDiary에서 추가", ""+position + enteredPlace +
                        enteredDate + enteredTimeS + enteredTimeE + enteredAddress + enteredX + enteredY)

                if (position != null && position >= 0) {
                    val item = diaryPlaceList[position]
                    item.place = enteredPlace
                    item.placeDate = enteredDate
                    item.placeTimeS = enteredTimeS
                    item.placeTimeE = enteredTimeE
                    item.address = enteredAddress
                    item.x = enteredX
                    item.y = enteredY

                    diaryPlaceAdapter.notifyItemChanged(position)
                } else {
                    if (!enteredPlace.isNullOrEmpty()) {
                        // DiaryPlaceModel 인스턴스를 생성하고 리스트에 추가
                        val newDiaryPlaceModel =
                            DiaryPlaceModel(place = enteredPlace, placeDate = enteredDate, placeTimeS = enteredTimeS,
                                placeTimeE = enteredTimeE, address = enteredAddress, x = enteredX, y = enteredY)
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

            val intent = Intent(this, AddDiaryMapActivity::class.java)
            intent.putExtra("itemPosition", -1)
            addPlaceActivityResult.launch(intent)
        }

        //메모 추가 버튼 클릭 시
        binding.diaryAddMemoBtn.setOnClickListener {

        }

        if (new != 1) {
            diaryId = intent.getIntExtra("diary_id", -1)
            Log.d("일기 수정", "" + diaryId)
            DiaryDetailManager.getDiaryDetailData(
                diaryId!!,
                onSuccess = {diaryDetailResponse ->
                    val editTitle = Editable.Factory.getInstance().newEditable(diaryDetailResponse.diaryDto.title)
                    val editTravelDest = Editable.Factory.getInstance().newEditable(diaryDetailResponse.diaryDto.travelDest)
                    val editHash = Editable.Factory.getInstance().newEditable(diaryDetailResponse.diaryDto.tags.joinToString(" ") { "#${it.name}" })
                    binding.diaryAddTitle.text = editTitle
                    binding.diaryAddDest.text = editTravelDest
                    binding.diaryAddHash.text = editHash

                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedStartDate = dateFormatter.format(diaryDetailResponse.diaryDto.travelStart)
                    val formattedEndDate = dateFormatter.format(diaryDetailResponse.diaryDto.travelEnd)

                    binding.diaryAddStart.text = formattedStartDate
                    binding.diaryAddEnd.text = formattedEndDate

                    val diaryDetailModels: MutableList<DiaryDetailModel> = mutableListOf()
                    if (diaryDetailResponse.diaryLocationDtoList != null && diaryDetailResponse.diaryLocationDtoList.isNotEmpty()) {
                        diaryDetailModels.addAll(diaryDetailResponse.diaryLocationDtoList.map { locationDetail ->
                            val formattedStartTime = SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).format(locationDetail.timeStart)
                            val formattedEndTime = SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).format(locationDetail.timeEnd)

                            DiaryDetailModel(
                                diaryLocationId = locationDetail.diaryLocationId,
                                diaryId = locationDetail.diaryId,
                                place = locationDetail.name,
                                content = locationDetail.content,
                                address = locationDetail.address,
                                x = locationDetail.x,
                                y = locationDetail.y,
                                placeDate = locationDetail.date,
                                placeStart = formattedStartTime, // timeStart를 원하는 형식으로 변환
                                placeEnd = formattedEndTime    // timeEnd를 원하는 형식으로 변환
                            )
                        })
                    }

                    diaryDetailResponse.diaryDto?.memo?.let { memo ->
                        if (memo.isNotEmpty()) {
                            diaryDetailModels.add(
                                DiaryDetailModel(
                                    diaryLocationId = -1,
                                    diaryId = diaryDetailResponse.diaryDto.diaryId,
                                    place = "MEMO",
                                    content = memo
                                )
                            )
                        }
                    }

                    diaryDetailAdapter.updateData(diaryDetailModels)

                },
                onError = { throwable ->
                    Log.e("서버 테스트3", "오류: $throwable")
                }
            )
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

    private fun saveDiaryToServer() { // 일기 서버에 추가
        val travelDest = binding.diaryAddDest.text.toString()
        val content = binding.diaryAddTitle.text.toString()
        val public = !binding.diaryAddLockBtn.isChecked
        val hashTagArray = binding.diaryAddHash.getInsertTag() ?: emptyArray()
        val tags: List<TagName> = hashTagArray.map { TagName(it) }
        val travelStart = binding.diaryAddStart.text.toString()
        val travelEnd = binding.diaryAddEnd.text.toString()

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

        // 여행지 목록에 "MEMO" 제외
        val filteredDiaryPlaceList = diaryPlaceList.filter { it.place != "MEMO" }

        // "MEMO" 항목을 처리할 수 있도록 따로 처리 코드 추가
        val memoItem = diaryPlaceList.find { it.place == "MEMO" }
        lateinit var memo :String

        if (memoItem?.content == "클릭하여 메모를 작성하세요.") {
            memo = ""
        } else {
            memo = memoItem?.content ?: ""
        }

        val diaryDto = DiaryDto(
            content, travelDest, memo, travelStartDate, travelEndDate, tags, public
        )

        val diaryLocations = mutableListOf<DiaryLocationDto>()

        for (item in filteredDiaryPlaceList) {
            val place = item.place ?: "여행지"
            lateinit var content :String
            val imageUris = item.imageUris
            val address = item.address ?: ""
            val x = item.x?: ""
            val y = item.y?: ""

            if (item.content == "클릭하여 여행지별 일기를 기록하세요.") {
                content = ""
            } else {
                content = item.content ?: ""
            }

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
                    name = place,
                    address = place,
                    x = x,
                    y = y,
                    date = placeDate,
                    timeStart = placeTimeStart,
                    timeEnd = placeTimeEnd,
                    diaryLocationImageDtoList = listOf() // 이미지 리스트 추가 필요
                )
                Log.d("kyumin", "" + diaryLocation)
                diaryLocations.add(diaryLocation)
            }
        }

        val diaryData = DiaryData(diaryDto,diaryLocations)

        // 저장된 토큰 읽어오기
        val sharedPreferences = getSharedPreferences("my_token", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        Log.d("서버 테스트", ""+diaryData)
        if (authToken != null) {
            DiaryManager.sendDiaryToServer(diaryData, authToken)
        }

    }
}