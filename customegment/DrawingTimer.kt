package az.myaccess.ui.activities.stories.customegment

import android.os.Handler
import java.lang.IllegalArgumentException


class DrawingTimer {
    private val handler: Handler = Handler()
    private var totalTicks = 0
    private var currentTick = 0
    private var listener: Listener? = null
    private var timerState: TimerState = TimerState.IDLE
    fun start(timeInMilliseconds: Long) {
        requireMinimumTickTime(timeInMilliseconds)
        if (timerState == TimerState.IDLE) {
            this.totalTicks =
                (timeInMilliseconds / TICK_TIME_MILLISECONDS).toInt()
        }
        if (timerState != TimerState.RUNNING) {
            timerState = TimerState.RUNNING
            runDrawingTask()
        }
    }

    private fun requireMinimumTickTime(timeInMilliseconds: Long) {
        if (timeInMilliseconds < DrawingTimer.Companion.TICK_TIME_MILLISECONDS) {
            val errorMessage = "A minimum of " +
                    DrawingTimer.Companion.TICK_TIME_MILLISECONDS +
                    " milliseconds is required, but input is " +
                    timeInMilliseconds + " milliseconds."
            throw java.lang.IllegalArgumentException(errorMessage)
        }
    }

    private fun runDrawingTask() {
        handler.post(object : Runnable {
            public override fun run() {
                listener?.onTick(currentTick, totalTicks)
                currentTick++
                if (currentTick <= totalTicks) {
                    handler.postDelayed(this, DrawingTimer.Companion.TICK_TIME_MILLISECONDS)
                } else {
                    reset()
                }
            }
        })
    }

    fun pause() {
        if (timerState == TimerState.RUNNING) {
            timerState = TimerState.PAUSED
            handler.removeCallbacksAndMessages(null)
        }
    }

    fun resume() {
        if (timerState == TimerState.PAUSED) {
            timerState = TimerState.RUNNING
            runDrawingTask()
        }
    }

    fun reset() {
        pause()
        timerState = TimerState.IDLE
        currentTick = 0
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun isRunning(): Boolean {
        return timerState == TimerState.RUNNING
    }

    fun isPaused(): Boolean {
        return timerState == TimerState.PAUSED
    }

    internal enum class TimerState {
        RUNNING, PAUSED, IDLE
    }

    interface Listener {
        fun onTick(currentTicks: Int, totalTicks: Int)
    }

    companion object {
        private const val TICK_TIME_MILLISECONDS: Long = 10
    }

}