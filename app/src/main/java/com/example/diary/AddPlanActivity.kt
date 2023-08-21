package com.example.diary

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aqoong.lib.hashtagedittextview.HashTagEditTextView
import com.example.diary.PlanManager.sendPlanToServer
import com.example.diary.databinding.ActivityAddPlanBinding
import retrofit2.http.POST
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale

class AddPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlanBinding
    private lateinit var viewModel: AddPlanViewModel
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // 여행지 데이터를 저장할 리스트
    private val planPlaceList = mutableListOf<PlanDetailModel>()
    private val planDetailAdapter = PlanDetailAdapter(planPlaceList)

    companion object {
        lateinit var planInMapActivityResult: ActivityResultLauncher<Intent>
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  //툴바에 뒤로 가기 버튼 추가

        //RecyclerView
        binding.planDetailRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddPlanActivity)
            adapter = planDetailAdapter
        }

        viewModel = ViewModelProvider(this).get(AddPlanViewModel::class.java)

        // AddPlaceInPlanActivity(지도)를 시작하기 위한 요청 코드 정의 (이미지 추가 필요)
        planInMapActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val position = data?.getIntExtra("itemPosition", -1)
                val enteredPlace = data?.getStringExtra("enteredPlace")
                val enteredAddress = data?.getStringExtra("enteredAddress")
                val enteredTel = data?.getStringExtra("enteredTel")
                val enteredDate = data?.getStringExtra("enteredDateS")
                val enteredStart = data?.getStringExtra("enteredTimeS")
                val enteredEnd = data?.getStringExtra("enteredTimeE")

                //val imageUris = data?.getParcelableArrayListExtra<Uri>("imageUris")
                Log.d("리사이클러뷰", ""+position)

                val placeDate: Date = try {
                    java.sql.Date(dateFormat.parse(enteredDate).time)
                } catch (e: Exception) { java.sql.Date(System.currentTimeMillis()) }

                if (position != null && position >= 0) {
                    val item = planPlaceList[position]
                    item.place = enteredPlace
                    //item.imageUris = imageUris
                    planDetailAdapter.notifyItemChanged(position)
                } else {
                    if (!enteredPlace.isNullOrEmpty()) {
                        // planDetailModel 인스턴스를 생성하고 리스트에 추가
                        val newPlanPlaceModel =
                            PlanDetailModel(place = enteredPlace, address = enteredAddress, tel = enteredTel,
                                placeDate = placeDate, placeStart = enteredStart, placeEnd = enteredEnd)
                        planPlaceList.add(newPlanPlaceModel)

                        // 어댑터에 데이터 변경을 알림
                        planDetailAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        binding.planDateStart.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, object: DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year:Int, month: Int, dayOfMonth: Int) {
                    binding.planDateStart.text = "${year}-${month+1}-${dayOfMonth}"
                }
            }, 2023, 9, 1)
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
        }

        binding.planDateEnd.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, object: DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year:Int, month: Int, dayOfMonth: Int) {
                    binding.planDateEnd.text = "${year}-${month+1}-${dayOfMonth}"
                }
            }, 2023, 9, 1)
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.primary))
        }

        // 저장하기 버튼 클릭 시
        binding.planSaveBtn.setOnClickListener {
            savePlanToServer()  // 서버로 데이터 전송
            finish() // 현재 액티비티 종료
        }

        //extension button 실행 후,
        val requestLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            //adapter.notifyDataSetChanged()
        }

        //extension button

        // 여행지 추가 버튼 클릭 시, 지도로 연결
        binding.placeAddNew.setOnClickListener {
            // 기존의 입력을 ViewModel에 저장
            viewModel.enteredTitle = binding.planTitleEdit.text.toString()
            viewModel.enteredSubTitle = binding.planSubtitleEdit.text.toString()
            viewModel.enteredStart = binding.planDateStart.text.toString()
            viewModel.enteredEnd = binding.planDateEnd.text.toString()
            viewModel.enteredHash = binding.planHashEdit.text.toString()
            viewModel.enteredClosed = binding.planLockBtn.isChecked

            val intent = Intent(this, AddPlanMapActivity::class.java)

            requestLauncher.launch(intent) //인텐트를 보내어 result로 데이터를 다시 받아옴
            //startActivity(intent)
            planInMapActivityResult.launch(intent) //지도로 이동
            //->setResult(Activity.RESULT_OK, intent)
            //->finish()
        }

    }

    // 입력된 데이터를 planData에 넣어서 전송 요청
    private fun savePlanToServer() {
        val travelDest = binding.planTitleEdit.text.toString()
        val content = binding.planSubtitleEdit.text.toString()
        val public = !binding.planLockBtn.isChecked
        val hashTagArray = binding.planHashEdit.getInsertTag() ?: emptyArray()
        val travelStart = binding.planDateStart.text.toString()
        val travelEnd = binding.planDateEnd.text.toString()

        val locations: List<Location> = planPlaceList.map { planDetail ->
            val timeStartUtil: java.util.Date = timeFormat.parse(planDetail.placeStart)
            val timeStartSql: Time = Time(timeStartUtil.time)

            val timeEndUtil: java.util.Date = timeFormat.parse(planDetail.placeEnd)
            val timeEndSql: Time = Time(timeEndUtil.time)
            Location(
                date = planDetail.placeDate,
                timeStart = timeStartSql,
                timeEnd = timeEndSql,
                name = planDetail.place ?: "",
                address = planDetail.address ?: ""
            )
        }

        val tags: List<Tag> = hashTagArray.map { Tag(it) }


        val travelStartDate: Date = try {
            java.sql.Date(dateFormat.parse(travelStart).time)
        } catch (e: Exception) { java.sql.Date(System.currentTimeMillis()) }

        val travelEndDate: Date = try {
            java.sql.Date(dateFormat.parse(travelEnd).time)
        } catch (e: Exception) { java.sql.Date(System.currentTimeMillis()) }

        val plan = Plan(travelDest, content, travelStartDate, travelEndDate, public)
        val planData = PlanData(plan, locations, tags)

        Log.d("서버 테스트", ""+planData)
        sendPlanToServer(planData)

//        // 날짜 초기화가 되었다면
//        if (travelStartDate != null && travelEndDate != null) {
//            val plan = Plan(travelDest, content, travelStartDate, travelEndDate, public)
//            val planData = PlanData(plan, locations, tags)
//
//            Log.d("서버 테스트", "" + planData)
//            sendPlanToServer(planData)
//        } else {
//            Log.e("서버 테스트", "여행 시작일과 종료일을 선택하세요.")
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 클릭 시
                finish() // 현재 액티비티 종료
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        // 이전에 입력한 텍스트를 복원하여 보여줌
        binding.planTitleEdit.setText(viewModel.enteredTitle)
        binding.planSubtitleEdit.setText(viewModel.enteredSubTitle)

        if (viewModel.enteredStart != null || viewModel.enteredEnd != null) {
            binding.planDateStart.setText(viewModel.enteredStart)
            binding.planDateEnd.setText(viewModel.enteredEnd)
        }

        if (viewModel.enteredHash != null) {
            binding.planHashEdit.setText(viewModel.enteredHash)
        }

        binding.planLockBtn.isChecked = viewModel.enteredClosed
    }

}
