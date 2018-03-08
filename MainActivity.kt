package com.example.nekuin.timer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    internal var x = 0
    internal var timerValue = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        titleText.text = "Game status: "


        val t = object : Thread() {
            override fun run() {

                while (!isInterrupted) {
                    try {
                        Thread.sleep(100)
                        runOnUiThread(object : Runnable {
                            override fun run() {
                                var minutes = 0
                                var seconds = timerValue
                                if (timerValue <= 0) {
                                    timerText.text = "No ongoing game"
                                    titleText.text = "Game status: "
                                    timerValue = 0
                                } else {
                                    while (seconds >= 60) {
                                        if (seconds >= 60) {
                                            minutes += 1
                                            seconds -= 60
                                        }
                                    }
                                    titleText.text = "Game ends in:  "
                                    //timerText.text = "" + minutes + ":" + seconds
                                    timerText.text = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
                                }
                            }
                        })
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

            }
        }
        t.start()


        startTimerButton.setOnClickListener {
            if (inputTimer.text.isNotEmpty()) {
                var input = inputTimer.text.toString().toInt()
                timerValue = input
                inputTimer.text.clear()
                toggleTimer()
            } else {
                timerValue = 0
                Toast.makeText(this, "input a value first!", Toast.LENGTH_SHORT).show()
            }

        }

        stopTimerButton.setOnClickListener {
            stopThread()
        }
    } // close onResume

    fun toggleTimer() {
        thread(start = true, name = "tingi") {
            while(timerValue >= 1){
                timerValue--
                Thread.sleep(1000)
            }
        }
    }

    fun stopThread() {
        timerValue = 0
    }
}
