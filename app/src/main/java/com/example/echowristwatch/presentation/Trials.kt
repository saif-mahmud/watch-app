package com.example.echowristwatch.presentation

import Utilities
import Utilities.TargetPoses
import android.util.Log

class Trials {
    private val tag = "Trials"
    var trialsDone: ArrayList<Trial>? = null // all the completed trials
    var trials: ArrayList<Trial?>? = null // all the trials


    constructor() {
        trials = ArrayList()
        trialsDone = ArrayList()
    }

    fun initTrialBlock(poses: Int, reps: Int): Boolean {
        trials = ArrayList()

        if (Utilities.BlockCounter > Utilities.NofBlocks) {
            return false
        }

        for (r in 0 until reps)
            for (p in 0 until poses){
                Log.i(tag, "Log/ TargetPose: " + TargetPoses[p])
                trials!!.add( Trial(TargetPoses[p]) )
            }
//        shuffleTrials()
        return true
    }

    private fun shuffleTrials() {
        if (trials != null)
            trials?.shuffle()
    }

    fun getCurrentPose(): Int {
        val currentTrial = trials!![0]
        return currentTrial!!.pose
    }

    fun startTrial(t: Long) {
        val currentTrial = trials!![0]
        currentTrial!!.trialStartTime = t
        Utilities.TrialCounter += 1
        Log.i(tag, "Log/ START TIME: $t")
    }

    fun endTrial(t: Long) {
        val currentTrial = trials!![0]
        currentTrial!!.trialEndTime = t
        Utilities.TrialEndCounter += 1
        Log.i(tag, "Log/ END TIME: $t")
//        trialsDone!!.add(Trial(currentTrial))
    }

    fun addAcc(data: String){
        val currentTrial = trials!![0]
//        currentTrial!!.AccList?.add(data)
        currentTrial!!.AccMsg.append(data).append(" ")
    }

    fun addGyr(data: String){
        val currentTrial = trials!![0]
//        currentTrial!!.GyrList?.add(data)
        currentTrial!!.GyrMsg.append(data).append(" ")
    }

    fun addMag(data: String){
        val currentTrial = trials!![0]
//        currentTrial!!.MagList?.add(data)
        currentTrial!!.MagMsg.append(data).append(" ")
    }

    fun getSensorString(): String {
        val currentTrial = trials!![0]
        return "${currentTrial!!.AccMsg},${currentTrial.GyrMsg},${currentTrial.MagMsg}"
    }

    fun getString(): String {
        val currentTrial = trials!![0]
        var Msg = Utilities.SUB_ID + "," + (Utilities.BlockCounter) + "," + (Utilities.TrialEndCounter-1) + "," + currentTrial!!.pose + "," +
                currentTrial.trialStartTime + "," + currentTrial.trialEndTime

        var sensorMsg = getSensorString();
        Msg += ",$sensorMsg";
        return Msg
    }
}

class Trial {
    // trial data
    var pose: Int

    // measures
    var trialStartTime: Long = 0    // starttime (processing)
    var trialEndTime: Long = 0      // endtime   (processing - usually shortly after touchup)

    var AccMsg: StringBuilder = StringBuilder()
    var GyrMsg: StringBuilder = StringBuilder()
    var MagMsg: StringBuilder = StringBuilder()

    constructor(p: Int) {
        pose = p
        trialStartTime = trialEndTime - 1
        AccMsg = StringBuilder()
        GyrMsg = StringBuilder()
        MagMsg = StringBuilder()
    }
}