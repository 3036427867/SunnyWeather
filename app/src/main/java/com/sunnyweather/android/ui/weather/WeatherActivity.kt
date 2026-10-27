package com.sunnyweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    lateinit var activityWeatherBinding: ActivityWeatherBinding

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        setContentView(R.layout.activity_weather)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        activityWeatherBinding= ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(activityWeatherBinding.root)

        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result->
            val weather=result.getOrNull()
            if(weather!=null){
                showWeatherInfo(weather)
            } else{
                Toast.makeText(this,"无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            activityWeatherBinding.swipeRefresh.isRefreshing=false
        })
        activityWeatherBinding.swipeRefresh.setColorSchemeResources(R.color.black)
        //刷新天气
        refreshWeather()
        activityWeatherBinding.swipeRefresh.setOnRefreshListener{
            refreshWeather()
        }
//        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)


        activityWeatherBinding.nowLayout.navBtn.setOnClickListener {
            activityWeatherBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        activityWeatherBinding.drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int){}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float){}
            override fun onDrawerOpened(drawerView: View){}
            override fun onDrawerClosed(drawerView: View) {
                val manager=getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

    }
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        activityWeatherBinding.swipeRefresh.isRefreshing=true
    }

    private fun showWeatherInfo(weather: Weather){
        activityWeatherBinding.nowLayout.placeName.text=viewModel.placeName
        val realtime=weather.realtime
        val daily=weather.daily
        //填充now。xml布局
        val currentTempText="${realtime.temperature.toInt()}°C"
        activityWeatherBinding.nowLayout.currentTemp.text=currentTempText
        activityWeatherBinding.nowLayout.currentSky.text= getSky(realtime.skycon).info
        val currentPM25Text="空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        activityWeatherBinding.nowLayout.currentAQI.text=currentPM25Text
        activityWeatherBinding.nowLayout.root.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast。xml布局中的数据
        activityWeatherBinding.forecastCard.forecastLayout.removeAllViews()
//        val forecastLayout=activityWeatherBinding.forecastCard.forecastLayout
        val days=daily.skycon.size
        for (i in 0 until days){
            val skycon=daily.skycon[i]
            val temperature=daily.temperature[i]
            val view= LayoutInflater.from(this)   /////小写大写
                .inflate(R.layout.forecast_item,activityWeatherBinding.forecastCard.forecastLayout,false)
            val dateInfo=view.findViewById(R.id.dateInfo) as TextView
            val skyIcon=view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo=view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo=view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text=simpleDateFormat.format(skycon.date)
            val sky= getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info
            val tempText="${temperature.min.toInt()}~${temperature.max.toInt()}°C"
            temperatureInfo.text=tempText
            activityWeatherBinding.forecastCard.forecastLayout.addView(view)
        }
        //填充life_index。xml布局中的数据
        val lifeIndex=daily.lifeIndex
        activityWeatherBinding.lifeIndexCard.coldRiskText.text=lifeIndex.coldRisk[0].desc
        activityWeatherBinding.lifeIndexCard.dressingText.text=lifeIndex.dressing[0].desc
        activityWeatherBinding.lifeIndexCard.ultravioletText.text=lifeIndex.ultraviolet[0].desc
        activityWeatherBinding.lifeIndexCard.carWashingText.text=lifeIndex.carWashing[0].desc
        activityWeatherBinding.weatherLayout.visibility = View.VISIBLE

    }
}