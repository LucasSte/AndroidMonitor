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
import android.os.RemoteException
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPath()
        goToGpuPage()
        getMemoryUsage()
        getProcessName()
        getMyPID()
        getDiskUsage()
        getNetworkUsage()
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

    private fun getProcessName()
    {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getMemoryUsage()
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
