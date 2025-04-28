package com.example.echowristwatch.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.echowristwatch.R

class BlockActivity : ComponentActivity() {
    private val TAG = "Log: BlockActivity"

    companion object {
        var trialSet = Trials()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val startBtn: Button = findViewById(R.id.main_button)

        startBtn.setOnClickListener {
            Log.w(TAG, "Log/ LeftBlocks: " + (Utilities.NofBlocks - Utilities.BlockCounter).toString())

            if (Utilities.BlockCounter < Utilities.NofBlocks) {
                Utilities.TrialCounter = 0
                if (!trialSet.initTrialBlock( Utilities.TargetPose, Utilities.TargetReps)) {
                    Log.w(TAG, "Log/ No more trials")
                } else {
                    Log.w(TAG, "Log/ Trials are generated: #" + trialSet.trials!!.size )

                    val tap = Intent(this@BlockActivity, TapActivity::class.java)
                    startActivity(tap)
                    finish()
                }
            }
        }
    }
}