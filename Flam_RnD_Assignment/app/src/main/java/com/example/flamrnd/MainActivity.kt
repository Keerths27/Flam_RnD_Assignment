package com.example.flamrnd

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.opengl.GLSurfaceView

class MainActivity : ComponentActivity() {

    companion object {
        init {
            System.loadLibrary("flam_native")
        }
    }

    // JNI – processes a single grayscale frame
    external fun processFrame(input: ByteArray, width: Int, height: Int): ByteArray

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var edgeRenderer: EdgeRenderer

    // Camera2 fields
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var cameraHandler: Handler? = null
    private var cameraThread: HandlerThread? = null

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glSurfaceView = findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        edgeRenderer = EdgeRenderer(this)
        glSurfaceView.setRenderer(edgeRenderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        // TODO: Later I will connect the camera preview pipeline here
        // and send frames to processFrame() for native image processing.
        checkCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        if (hasCameraPermission()) {
            startCameraThread()
            openCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        closeCamera()
        stopCameraThread()
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission() {
        if (hasCameraPermission()) {
            startCameraThread()
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCameraThread() {
        if (cameraThread != null) return
        cameraThread = HandlerThread("CameraBackground").also {
            it.start()
            cameraHandler = Handler(it.looper)
        }
    }

    private fun stopCameraThread() {
        cameraThread?.quitSafely()
        try {
            cameraThread?.join()
        } catch (_: InterruptedException) {
        }
        cameraThread = null
        cameraHandler = null
    }

    private fun openCamera() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = manager.cameraIdList.firstOrNull() ?: return
            if (!hasCameraPermission()) return

            // Set up ImageReader to receive YUV frames
            val width = 640
            val height = 480
            imageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2).apply {
                setOnImageAvailableListener({ reader ->
                    val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                    val yPlane = image.planes[0]
                    val buffer = yPlane.buffer
                    val data = ByteArray(buffer.remaining())
                    buffer.get(data)
                    val w = image.width
                    val h = image.height
                    image.close()

                    // Process frame in native (Canny) – grayscale in, grayscale out
                    val processed = processFrame(data, w, h)

                    // Pass to renderer on GL thread
                    glSurfaceView.queueEvent {
                        edgeRenderer.updateFrame(processed, w, h)
                        glSurfaceView.requestRender()
                    }
                }, cameraHandler)
            }

            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    cameraDevice = device
                    createCaptureSession()
                }

                override fun onDisconnected(device: CameraDevice) {
                    device.close()
                    cameraDevice = null
                }

                override fun onError(device: CameraDevice, error: Int) {
                    device.close()
                    cameraDevice = null
                    Toast.makeText(this@MainActivity, "Camera error: $error", Toast.LENGTH_SHORT)
                        .show()
                }
            }, cameraHandler)
        } catch (e: SecurityException) {
            // Permission issue
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createCaptureSession() {
        val device = cameraDevice ?: return
        val readerSurface = imageReader?.surface ?: return

        try {
            val requestBuilder =
                device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(readerSurface)
                    set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                }

            device.createCaptureSession(
                listOf(readerSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        try {
                            session.setRepeatingRequest(
                                requestBuilder.build(),
                                null,
                                cameraHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(
                            this@MainActivity,
                            "Camera configuration failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                cameraHandler
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        captureSession?.close()
        captureSession = null
        cameraDevice?.close()
        cameraDevice = null
        imageReader?.close()
        imageReader = null
    }
}
