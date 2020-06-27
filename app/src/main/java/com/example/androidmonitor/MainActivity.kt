package com.example.androidmonitor

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
        curPath.text = path.toString()
    }
}
