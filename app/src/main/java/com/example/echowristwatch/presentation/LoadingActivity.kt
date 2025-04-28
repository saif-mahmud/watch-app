package com.example.echowristwatch.presentation

import DataPlayer
import DataRecorder.dataRecorder
import Utilities.RecordingTime
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.echowristwatch.R
import com.example.echowristwatch.presentation.MainActivity.Companion.player
import com.example.echowristwatch.presentation.MainActivity.Companion.scifiChirpWav

class LoadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        player.play_repeat()
        dataRecorder.startStreamingAudio(Utilities.IP, RecordingTime, Utilities.TrialEndCounter)

        Handler(Looper.getMainLooper()).postDelayed({
            val capture = Intent(this@LoadingActivity,CaptureActivity::class.java)
            startActivity(capture)
            finish()
        }, 1000)
    }
}