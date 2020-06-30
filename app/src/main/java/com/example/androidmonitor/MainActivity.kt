package com.example.androidmonitor

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPath()
        goToGpuPage()
        getMemoryUsage()
    }

    private fun goToGpuPage()
    {
        gpuButton.setOnClickListener {
            val intent = Intent(this, GpuInfo::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private fun getPath()
    {
        val path = System.getProperty("user.dir")
        val p = packageManager.getPackageInfo(packageName, 0)
        //curPath.text = path.toString()
        curPath.text = p.applicationInfo.dataDir
    }

    private fun getMemoryUsage()
    {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val availableMegs = mi.availMem / 0x100000L
        val percentageAvail = mi.availMem / mi.totalMem.toDouble()  * 100

        availMbText.text = availableMegs.toString()
        availMemPer.text = percentageAvail.toString()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getMemoryUsage()
    }
}
