package com.example.androidmonitor

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cpu_info.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.RandomAccessFile

class CpuInfo : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cpu_info)
        getNumberOfCores()
        getAbi()
        getHardwareName()
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
//            Pair(-1, -1)
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getAbi() {
        abiTV.text = Build.SUPPORTED_ABIS[0]
    }

    fun getHardwareName(){
        val br = BufferedReader(FileReader("/proc/cpuinfo"))

        var str: String? = null

        val output: MutableMap<String, String> = HashMap()

        while (br.readLine().also({ str = it }) != null) {
            val data = str?.split(":".toRegex())?.toTypedArray()
            if (data != null) {
                if (data.size > 1) {
                    var key = data[0].trim { it <= ' ' }.replace(" ", "_")
                    if (key == "Hardware") key = "cpu_model"
                    output[key] = data[1].trim { it <= ' ' }
                }
            }
        }
        br.close()
        cpuNameTV.text = output["cpu_model"]

    }

}