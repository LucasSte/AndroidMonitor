package com.example.androidmonitor

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPath()
        goToGpuPage()
        getCpuUsage()
        getThread()
        CoroutineScope(Dispatchers.Default).launch {
            updateEverySec()
        }
        getProcessName()
        getMyPID()
        getNetworkUsage()
        goToStaticPage()
        setThreadPriority()
    }

    private fun goToStaticPage()
    {
        goToStatic.setOnClickListener {
            val intent = Intent(this, StaticInfo::class.java)
            startActivity(intent)
        }
    }

    private suspend fun updateEverySec() = withContext(Dispatchers.Default)
    {
        while (true)
        {
            getMemUsage()
            getThread()
            getCpuUsage()
            Thread.sleep(1000)
        }
    }

    private fun getMemUsage() {
        //Memory usage
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val availableMegs = mi.availMem / 0x100000L
        val percentageAvail = mi.availMem / mi.totalMem.toDouble()  * 100
        runOnUiThread {
            availMbText.text = availableMegs.toString()
            availMemPer.text = percentageAvail.toString()
        }
    }

    private fun goToGpuPage()
    {
        gpuButton.setOnClickListener {
            val intent = Intent(this, GpuInfo::class.java)
            startActivity(intent)
        }
    }

    private fun getPath()
    {
        val path = System.getProperty("user.dir")
        val p = packageManager.getPackageInfo(packageName, 0)
        //curPath.text = path.toString()
        curPath.text = p.applicationInfo.dataDir
    }


    private fun getProcessName()
    {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager =
            this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                processName = processInfo.processName
                break
            }
        }
        processNameView.text = processName
    }

    private fun getMyPID()
    {
        PIDView.text = android.os.Process.myPid().toString()
    }

    private fun getNetworkUsage()
    {
        // TODO: Implement
    }

    private fun getCpuUsage()
    {
        var cpuUsage = getCpuUsageStatistic()
        totalValue.text = cpuUsage!![1].toString()
        runningValue.text = cpuUsage[3].toString()
        sleepingValue.text = cpuUsage[5].toString()
        stoppedValue.text = cpuUsage[7].toString()
        zombieValue.text = cpuUsage[9].toString()
    }

    /**
     *
     * @return integer Array with 4 elements: user, system, idle and other cpu
     * usage in percentage.
     */
    private fun getCpuUsageStatistic(): IntArray? {
        var tempString = executeTop()
        tempString = tempString!!.replace(",".toRegex(), "")
        tempString = tempString.replace("User".toRegex(), "")
        tempString = tempString.replace("System".toRegex(), "")
        tempString = tempString.replace("IOW".toRegex(), "")
        tempString = tempString.replace("IRQ".toRegex(), "")
        tempString = tempString.replace("%".toRegex(), "")
        for (i in 0..9) {
            tempString = tempString!!.replace("  ".toRegex(), " ")
        }
        tempString = tempString!!.trim { it <= ' ' }
        var myString = tempString.split(" ".toRegex()).toTypedArray()
        var cpuUsageAsInt = IntArray(myString.size)
        for (i in myString.indices) {
            if (myString[i].length == 1) {
                myString[i] = myString[i].trim { it <= ' ' }
                cpuUsageAsInt[i] = myString[i].toInt()
            }
        }
        return cpuUsageAsInt
    }

    private fun executeTop(): String? {
        var p: Process? = null
        var `in`: BufferedReader? = null
        var returnString: String? = null
        try {
            p = Runtime.getRuntime().exec("top -n 1")
            `in` = BufferedReader(InputStreamReader(p.inputStream))
            while (returnString == null || returnString.contentEquals("")) {
                returnString = `in`.readLine()
            }
        } catch (e: IOException) {
            Log.e("executeTop", "error in getting first line of top")
            e.printStackTrace()
        } finally {
            try {
                `in`!!.close()
                p!!.destroy()
            } catch (e: IOException) {
                Log.e(
                    "executeTop",
                    "error in closing and destroying top process"
                )
                e.printStackTrace()
            }
        }
        return returnString
    }

    private fun getThread() {
        runOnUiThread() {
            currentThread.text = Thread.currentThread().toString()
            currentThreadCount.text = Thread.activeCount().toString()
            currentThreadPriority.text = Thread.currentThread().priority.toString()
        }
    }

    private fun setThreadPriority() {
        upPriority.setOnClickListener(){
            if(Thread.currentThread().priority < 11)
            {
                Thread.currentThread().priority++
            }
        }

        downPriority.setOnClickListener() {
            if(Thread.currentThread().priority > 1)
            {
                Thread.currentThread().priority--
            }
        }

        getThread()
    }

    override fun onResume() {
        super.onResume()
        getCpuUsage()
        getThread()
    }
}
