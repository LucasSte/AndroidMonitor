package com.example.androidmonitor

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.os.RemoteException
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
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
        getDiskUsage()
        getNetworkUsage()
        goToStaticPage()
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
            Thread.sleep(1000)
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

    private fun getDiskUsage()
    {
        // TODO: Implement
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkUsage():Int {
        // Todo: Verify permissions
//        if (!haveStatsPermissions()) {
//            wifiUsageView.text = "No permission"
//            return -1
//        }
        getWifiRate()
        return 0
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getWifiRate():Int
    {
        val globalBucket = getWifiGlobalRate()

        wifiUploadView.text = (bucket.txBytes.toDouble()/(bucket.endTimeStamp-bucket.startTimeStamp).toDouble()).toString();
        wifiDownloadView.text = (bucket.rxBytes.toDouble()/(bucket.endTimeStamp-bucket.startTimeStamp).toDouble()).toString();

        return 0

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getWifiGlobalRate():NetworkStats.Bucket {
        val networkStatsManager =
            applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        val bucket: NetworkStats.Bucket
        bucket = try {
            networkStatsManager.querySummaryForDevice(
                NetworkCapabilities.TRANSPORT_WIFI,
                "",
                0,
                System.currentTimeMillis()
            )
        } catch (e: RemoteException) {
            return NetworkStats.Bucket()
        }
        return bucket
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
        print(myString)
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
        currentThread.text = Thread.currentThread().toString()
        currentThreadCount.text = Thread.activeCount().toString()
    }


    override fun onResume() {
        super.onResume()
        this.getCpuUsage()
        this.getThread()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun haveStatsPermissions():Boolean
    {
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.PACKAGE_USAGE_STATS) !=
            PackageManager.PERMISSION_GRANTED)
        {
            println(ContextCompat.checkSelfPermission(this, android.Manifest.permission.PACKAGE_USAGE_STATS))
            println(PackageManager.PERMISSION_GRANTED)
            // Ask user permission
            // TODO: Fix: It's always asking user permission
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)

            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.PACKAGE_USAGE_STATS) !=
                PackageManager.PERMISSION_GRANTED)
            {
                return false
            }
        }
        return true

    }
}
