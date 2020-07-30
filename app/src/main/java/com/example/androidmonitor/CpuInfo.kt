package com.example.androidmonitor

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_cpu_info.*

class CpuInfo : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cpu_info)
        getNumberOfCores()
        getAbi()
        goBack()
    }

    private fun goBack() {
        backButton.setOnClickListener { finish() }
    }

    private val CPU_INFO_DIR = "/sys/devices/system/cpu/"


    fun getNumberOfCores(){
        coresTV.text = Runtime.getRuntime().availableProcessors().toString()
    }

//    fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
//        val minPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/cpuinfo_min_freq"
//        val maxPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/cpuinfo_max_freq"
//        return try {
//            val minMhz = RandomAccessFile(minPath, "r").use { it.readLine().toLong() / 1000 }
//            val maxMhz = RandomAccessFile(maxPath, "r").use { it.readLine().toLong() / 1000 }
//            Pair(minMhz, maxMhz)
//        } catch (e: Exception) {
//            Timber.e(e)
//            Pair(-1, -1)
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getAbi() {
        abiTV.text = Build.SUPPORTED_ABIS[0]
    }
}