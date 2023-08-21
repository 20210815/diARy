package com.example.diary

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog.show
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import android.widget.DatePicker
import android.widget.SearchView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.diary.databinding.ActivityAddDiaryMapBinding
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class AddDiaryMapActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddDiaryMapBinding
    lateinit var keyword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDiaryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //webview
        binding.webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true //자바스크립트 허용
            settings.loadWithOverviewMode = true //html의 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 자동 조정
            settings.useWideViewPort = true //html의 viewport 메타 태그 지원
            settings.setSupportZoom(false)
        }

        binding.webview.addJavascriptInterface(AddDiaryMapInterface(this), "Android")

        binding.webview.loadUrl("https://diarymap.netlify.app/AddSpot.html") //임시 주소
    }

    //toolbar 뒤로가기 버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //searchView 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val searchView = binding.searchview
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("mylog", "검색어 : $query")

                if (query !== null) {
                    keyword = query

                    //키보드 내리기
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                    binding.webview.loadUrl("javascript:diaryAddSpot.searchPlaces()")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    inner class AddDiaryMapInterface(val Context: Context) {
        //여행지 정보
        var title: String? = null
        var address: String? = null
        var tel: String? = null
        var x: String? = null
        var y: String? = null

        //일정 정보 - 초기값: 현재 날짜 및 시간
        var dateS: Array<Int>? = null
        var dateE: Array<Int>? = null
        var timeS: Array<Int>? = null
        var timeE: Array<Int>? = null

        val itemPosition = intent.getIntExtra("itemPosition", -1)

        @JavascriptInterface
        fun showToast(toast: String) {
            Log.d("mylog", toast)
        }

        @JavascriptInterface
        fun setPlaceInfo(title: String?, address: String?, tel: String?, x: String?, y: String?) {
            this.title = title
            this.address = address
            this.tel = tel
            this.x = x
            this.y = y

            Log.d(
                "mylog",
                "Save successed - ${this.title} / ${this.address} / ${this.tel} / ${this.x} / ${this.y}"
            )
        }

        @JavascriptInterface
        fun getSearchResult(): String {
            return keyword
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @JavascriptInterface
        fun setTripDate() {
            val dialog = AddDiaryMapDialog(this@AddDiaryMapActivity)
            dialog.myDialog(dateS, dateE, timeS, timeE)

            dialog.setOnClickedListener(object: AddDiaryMapDialog.ButtonClickListener {
                override fun onClicked(dateS_d: Array<Int>, dateE_d: Array<Int>, timeS_d: Array<Int>, timeE_d: Array<Int>) {
                    dateS = dateS_d
                    dateE = dateE_d
                    timeS = timeS_d
                    timeE = timeE_d
                }
            })
        }

        @JavascriptInterface
        fun addPlaceIn() {
            Log.d("mylog", "add successed")

            val placeDate = "${dateS?.get(0)}-${dateS?.get(1)}-${dateS?.get(2)}"
            val placeStart = "${timeS?.get(0)}:${timeS?.get(1)}"
            val placeEnd = "${timeE?.get(0)}:${timeE?.get(1)}"

            // 데이터를 이전 활동으로 전달하기 위한 인텐트 생성
            val intent = Intent()
            intent.putExtra("itemPosition", itemPosition) // position 전달
            intent.putExtra("enteredPlace", title)
            intent.putExtra("enteredDate", placeDate)
            intent.putExtra("enteredStart", placeStart)
            intent.putExtra("enteredEnd", placeEnd)

            Log.d("mylog", "여행지 정보 in 지도" + itemPosition + title + placeDate + placeStart +placeEnd)

            // 결과를 설정하고 현재 활동 종료
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}