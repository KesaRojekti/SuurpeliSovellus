package com.example.nekuin.timer


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment1.*
import java.util.*
import kotlin.concurrent.thread


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment1 : Fragment() {

    var timerValue = 0
    var minutes = 0
    var seconds = 0
    var i = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment1, container, false)
    }



    override fun onResume() {
        super.onResume()

/*
kopioitu netistä mutta, tietääkseni runOnUiThread on toisen threadin sisällä koska ensinäkin tavitsin Thread.sleep
ja toiseksi jos käytät esim Thread.sleep suoraan main threadissä UI "lockkaantuu" ajaksi jonka määritit
jos UI on ns blockkaantunut yli 5 sek appi taitaa crashata, jonkun keksimä ominaisuus
 */

        val t = object : Thread() {
            override fun run() {

                while (!isInterrupted) {
                    try {
                        Thread.sleep(100)
                        activity?.runOnUiThread(object : Runnable {
                            override fun run() {
                                if(timerValue <= 0){
                                    try {
                                        titleText.text = "Game status:"
                                        timerText.text = "no ongoing game"
                                    } catch (e: IllegalStateException){
                                        e.printStackTrace()
                                    }
                                } else {
                                    titleText.text = "Game ends in: "
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


        startButton.setOnClickListener {
            stopTimer()
            if (inputTimer.text.isNotEmpty()) {
                startTimer()
                inputTimer.text.clear()
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)

            } else {
                Toast.makeText(activity, "input a value first!", Toast.LENGTH_SHORT).show()
            }

        }

        stopButton.setOnClickListener {
            stopTimer()
        }
        //toisen aktiviteetin starttauksen testaus - Intent(tämä.activity, toinen.activity::class.java)
        navigateButton.setOnClickListener{
            val intent = Intent(activity, Main2Activity::class.java)
            startActivity(intent)
        }


    } // onResume

    override fun onPause() {
        super.onPause()
        stopTimer()
    }


    fun startTimer() {
        val timer = inputTimer.text.toString().toInt()
        this.timerValue = timer

        thread(start = true, name = "tingi") {

            while (this.timerValue >= 1) { //converttaa sekunnit minuuteiksi+sekunneiksi
                this.seconds = this.timerValue
                this.timerValue--

                this.minutes = 0
                while (seconds >= 60) {
                    minutes++
                    seconds -= 60
                }
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException){
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopTimer() {
        this.timerValue = 0
    }


}// class



