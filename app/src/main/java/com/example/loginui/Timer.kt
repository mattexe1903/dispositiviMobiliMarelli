package com.example.loginui

import android.os.Handler
import java.time.Duration
import java.time.LocalTime

class Timer(private val handler: Handler, private val updateCallback: (Long) -> Unit) {
    private var runnable: Runnable? = null
    private var elapsedTime: Long = 0
    private var isActive: Boolean = false
    private var lastIntermediateTime: LocalTime? = null

    fun start() {
        if (isActive) return
        isActive = true
        runnable = Runnable {
            elapsedTime += 1000
            updateCallback(elapsedTime)
            handler.postDelayed(runnable!!, 1000)
        }
        handler.post(runnable!!)
    }

    fun stop() {
        isActive = false
        handler.removeCallbacks(runnable!!)
    }

    fun reset() {
        stop()
        elapsedTime = 0
        updateCallback(elapsedTime)
    }

    fun intermediate() {
        val now = LocalTime.now()
        if (lastIntermediateTime != null) {
            val elapsedMillis = Duration.between(lastIntermediateTime, now).toMillis()
            elapsedTime += elapsedMillis
            updateCallback(elapsedTime)
        }
        lastIntermediateTime = now
    }

    fun getElapsedTime(): Long {
        return elapsedTime
    }
}

