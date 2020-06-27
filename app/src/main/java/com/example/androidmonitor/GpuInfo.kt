package com.example.androidmonitor

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_gpu_info.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10

class GpuInfo : AppCompatActivity(), GLSurfaceView.Renderer {
    private var glSurfaceView : GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpu_info)

        glSurfaceView = GLSurfaceView(this)
        glSurfaceView!!.setRenderer(this)

        (extensionsText.parent as ViewGroup).addView(this.glSurfaceView)
        goBack()
    }

    private fun goBack()
    {
        backButton.setOnClickListener {
            finish()
        }
    }


    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        val renderer = p0!!.glGetString(GL10.GL_RENDERER)
        val vendor = p0.glGetString(GL10.GL_VERSION)
        val version = p0.glGetString(GL10.GL_VERSION)
        val extensions = p0.glGetString(GL10.GL_EXTENSIONS)
        runOnUiThread{
            rendererText.text = renderer
            vendorText.text = vendor
            versionText.text = version
            extensionsText.text = extensions
            glSurfaceView!!.visibility = View.GONE
        }


    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {

    }

    override fun onDrawFrame(p0: GL10?) {

    }
}
