package com.example.diary

import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.aqoong.lib.hashtagedittextview.HashTagEditTextView
import com.example.diary.PlanManager.sendPlanToServer
import com.example.diary.databinding.ActivityAddPlanBinding
import retrofit2.http.POST
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class AddPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlanBinding

    // 여행지 데이터를 저장할 리스트
    private val planPlaceList = mutableListOf<DiaryPlaceModel>()
    //private val planPlaceAdapter = PlanAdapter()

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
            //adapter =
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
        binding.placeAddNew.setOnClickListener {
            val intent = Intent(this, AddPlanMapActivity::class.java)
            requestLauncher.launch(intent) //인텐트를 보내어 result로 데이터를 다시 받아옴
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

        val locations: List<Location> = emptyList() // 여행지별 카드, 지도 연결 후 추가 필요
        val tags: List<Tag> = hashTagArray.map { Tag(it) }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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

}
