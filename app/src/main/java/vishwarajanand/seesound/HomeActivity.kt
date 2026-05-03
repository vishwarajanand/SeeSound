package vishwarajanand.seesound

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import kotlin.math.sin

class HomeActivity : AppCompatActivity() {

    private var visualizerView: VisualizerView? = null
    private var recorder: MediaRecorder? = null
    private val handler = Handler(Looper.getMainLooper())
    private var txtLbl: TextView? = null
    
    private var isDemoMode = false
    private var demoTick = 0.0

    private val updater = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 50)
            
            val amplitude = if (isDemoMode) {
                // Generate a sine wave for demo mode
                demoTick += 0.2
                ((sin(demoTick) + 1.0) * 10000).toInt()
            } else {
                recorder?.maxAmplitude ?: 0
            }

            visualizerView?.let { vis ->
                vis.addAmplitude(amplitude)
                if (amplitude > 500) {
                    txtLbl?.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_home)

        visualizerView = findViewById(R.id.visualizer)
        txtLbl = findViewById(R.id.app_label)

        visualizerView?.setOnClickListener {
            startAudioRecorder()
        }
        
        visualizerView?.setOnLongClickListener {
            isDemoMode = !isDemoMode
            val msg = if (isDemoMode) "Demo Mode ON" else "Demo Mode OFF"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                APP_PERMISSIONS_RECORD_AUDIO,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == APP_PERMISSIONS_RECORD_AUDIO) {
            if ((grantResults.isNotEmpty()) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startAudioRecorder()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun startAudioRecorder() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        if (recorder != null) return

        try {
            recorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    val file = File.createTempFile("temp_audio", ".3gp", cacheDir)
                    setOutputFile(file.absolutePath)
                } catch (e: IOException) {
                    setOutputFile("/dev/null")
                }
                prepare()
                start()
            }
            Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Error starting recorder", ex)
            Toast.makeText(this, "Audio Init Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updater)
        stopRecorder()
    }
    
    private fun stopRecorder() {
        recorder?.apply {
            try {
                stop()
                reset()
                release()
            } catch (ex: Exception) {
                Log.e(LOG_TAG, "Error stopping recorder", ex)
            }
        }
        recorder = null
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            handler.post(updater)
        } else {
            handler.removeCallbacks(updater)
        }
    }

    companion object {
        private const val LOG_TAG = "HomeActivity"
        private const val APP_PERMISSIONS_RECORD_AUDIO = 1
    }
}
