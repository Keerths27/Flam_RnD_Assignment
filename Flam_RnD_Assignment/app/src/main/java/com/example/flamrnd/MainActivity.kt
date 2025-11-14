package com.example.flamrnd

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.opengl.GLSurfaceView

class MainActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var edgeRenderer: EdgeRenderer

    companion object {
        init {
            System.loadLibrary("flam_native")
        }
    }

    external fun processFrame(input: ByteArray, width: Int, height: Int): ByteArray

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
            } else {
                // Here you would start camera capture and feed frames to processFrame()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glSurfaceView = findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        edgeRenderer = EdgeRenderer(this)
        glSurfaceView.setRenderer(edgeRenderer)

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Camera permission already granted, start camera here (not fully implemented)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
