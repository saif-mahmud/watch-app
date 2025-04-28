/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.echowristwatch.presentation

import DataPlayer
import DataRecorder.sendMsgString
import SoundGenerator
import SoundGenerator.tileWaveArrays
import Utilities.BlockCounter
import Utilities.RecordingTime
import Utilities.TrialCounter
import Utilities.TrialEndCounter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.example.echowristwatch.R

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    companion object {
        lateinit var scifiChirpWav: DoubleArray
        lateinit var player: DataPlayer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // View Allocation
        var textIP: EditText = findViewById<EditText>(R.id.et_ip).also {
            it.setText(Utilities.IP)
        }
        var textSubnum: EditText = findViewById<EditText>(R.id.et_subnum)
        val startBtn: Button = findViewById(R.id.btn_start)

        val scifiChirp = SoundGenerator.loadWavAsDoubleArray(this, R.raw.chirp_18_21_48_600)
        scifiChirpWav = tileWaveArrays(scifiChirp, 600, 1);
        Log.w(TAG, "Log/ : " + scifiChirpWav.size )
        player = DataPlayer(scifiChirpWav, (1));

        BlockCounter = 0;
        TrialCounter = 0;
        TrialEndCounter = 0;

        startBtn.setOnClickListener {
            Utilities.IP = textIP.getText().toString()
            Utilities.SUB_ID = textSubnum.getText().toString()

            val MSG = "SUBID" + Utilities.leftPad(Utilities.SUB_ID, 5) + Utilities.getDateTS()
            sendMsgString(Utilities.IP, MSG)

            Log.w(TAG, "Log/ IP: " + Utilities.IP + ", MSG: " + MSG)

            val intent = Intent(this@MainActivity, BlockActivity::class.java)
            startActivity(intent)

            finish()
        }


    }
}