package com.example.echowristwatch.presentation

import DataRecorder.dataRecorder
import Utilities.RecordingTime
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.echowristwatch.R
import com.example.echowristwatch.presentation.BlockActivity.Companion.trialSet
import com.example.echowristwatch.presentation.MainActivity.Companion.player

class CaptureActivity: ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setUpSensorStuff()

        var poseText = findViewById<TextView>(R.id.poseText)
//        var poseImg  = findViewById<ImageView>(R.id.poseImg)

        var pose = trialSet.getCurrentPose()

        when (pose) {
            0 -> {
                poseText.text = "Capturing Data"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose5))
            }
//            1 -> {
//                poseText.text = "ASL-1"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose1))
//            }
//            2 -> {
//                poseText.text = "ASL-2"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose2))
//            }
//            3 -> {
//                poseText.text = "ASL-3"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose3))
//            }
//            4 -> {
//                poseText.text = "ASL-4"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose4))
//            }
//            5 -> {
//                poseText.text = "ASL-5"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose5))
//            }
//            6 -> {
//                poseText.text = "Flexion"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose6))
//            }
//            7 -> {
//                poseText.text = "Extension"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose7))
//            }
//            8 -> {
//                poseText.text = "Deviation"
//                poseImg.setImageDrawable(getDrawable(R.drawable.pose8))
//            }
        }

        trialSet.startTrial(System.currentTimeMillis())

        Handler(Looper.getMainLooper()).postDelayed({
            endActivity()
        }, (RecordingTime * 1000).toLong())
    }

    fun endActivity() {
        setOffSensorStuff()
        player.stop()
        trialSet.endTrial(System.currentTimeMillis())
        dataRecorder.stopStreamingAudio()
        val saving = Intent(this@CaptureActivity,SavingActivity::class.java)
        startActivity(saving)
        finish()
    }

    private fun setUpSensorStuff(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
            sensorManager.registerListener(this,
                it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also{
            sensorManager.registerListener(this,
                it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also{
            sensorManager.registerListener(this,
                it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    private fun setOffSensorStuff(){
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val timestamp = System.currentTimeMillis()
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            trialSet.addAcc("$x $y $z $timestamp" )
        }
        else if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE){
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            trialSet.addGyr( "$x $y $z $timestamp" )
        }
        else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            trialSet.addMag( "$x $y $z $timestamp" )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}