package com.example.echowristwatch.presentation

import DataRecorder.sendMsgString
import Utilities.IsRecordingIMU
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.echowristwatch.R
import com.example.echowristwatch.presentation.BlockActivity.Companion.trialSet

class SavingActivity : ComponentActivity() {
    private val TAG = "Saving Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saving)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        Log.w(TAG, "Log/ Left Trials: #${Utilities.NofTrials-Utilities.TrialCounter}, done Trials: #${Utilities.TrialCounter}, total done: #${Utilities.TrialEndCounter}")

        Handler(Looper.getMainLooper()).postDelayed({
            if (IsRecordingIMU) {
                val msg = trialSet.getString()
                sendMsgString(Utilities.IP, "BLOCK:$msg")
                Log.w(TAG, "LOG/ IP: " + Utilities.IP + ", MSG SENT")
            }
            val tap = Intent(this@SavingActivity,TapActivity::class.java)
            val block = Intent(this@SavingActivity,BlockActivity::class.java)

            if (Utilities.NofTrials-Utilities.TrialCounter == 0){
                Utilities.BlockCounter += 1
                startActivity(block)
            } else {
                trialSet.trials?.removeAt(0)
                startActivity(tap)
            }
            finish()
        }, 500)
    }
}