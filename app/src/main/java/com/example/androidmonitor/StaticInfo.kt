package com.example.androidmonitor

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import kotlinx.android.synthetic.main.activity_static_infos.*

class StaticInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_static_infos)

        goBack()
        loadTotalRam()
        getScreenResolution()
    }

    private fun goBack()
    {
        goBack.setOnClickListener {
            finish()
        }
    }

    private fun loadTotalRam()
    {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        totalRamTv.text = (mi.totalMem.toDouble() / 1073741824).toString()
    }

    private fun getScreenResolution()
    {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayResTv.text = displayMetrics.widthPixels.toString() + " X " + displayMetrics.heightPixels.toString()
    }
}
