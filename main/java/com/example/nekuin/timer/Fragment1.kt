package com.example.nekuin.timer


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment1.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * INFO screen
 */
class Fragment1 : Fragment() {

    private val webapp = WebAppInterface(this.context)
    var i = 0
    var hasData = false
    var updateTimer = false
    var cameFromPause = false
    var data: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment1, container, false)
    }



    override fun onResume() {
        super.onResume()
        updateTimer = true


        navigateButton.setOnClickListener {
            val intent:Intent = Intent(this.context, Main2Activity::class.java)
            startActivity(intent)
        }



        //javascript interface timer
        webSendData.getSettings().setJavaScriptEnabled(true)
        webSendData.addJavascriptInterface(webapp, "Android")
        webSendData.loadUrl("http://rframe.sytes.net:8080/timer.php")

        //depencies implementation "org.jetbrains.anko:anko-commons:0.10.4" (build.gradle)
        doAsync {
            //prevent duplicate threads doing the same thing(duplicate timers)
            if (!cameFromPause) {

                //keep trying to get data
                while (!hasData) {
                    hasData = webapp.dataCheck()
                }


                //when you have the data, do stuff with it
                if (hasData) {
                    data = webapp.data
                    var x: Int = data.toInt()
                    Log.d("data to int", "value: (int)" + x)
                    while (x > 0) {
                        x--;
                        Thread.sleep(1000)
                        uiThread {
                            if (updateTimer) {
                                if(x != 0){
                                    timerText.text = x.toString()
                                }
                                else
                                {
                                    timerText.text = getString(R.string.gameover)
                                }
                            }
                        }
                    }
                }
            }
        }




        //old timer
        webView.getSettings().setJavaScriptEnabled(true)
        webView.setBackgroundColor(1)
        //webView.loadUrl("http://suurpeli.samuliraty.fi/")
        webView.loadUrl("http://rframe.sytes.net:8080/timer")
        titleText.text = "Seuraavan pelin alkuun:"
    } // onResume

    override fun onPause() {
        super.onPause()
        //prevent duplicate timer values
        cameFromPause = true
        updateTimer = false
    }

    override fun onDestroy() {
        super.onDestroy()
        //prevent duplicate timer values
        cameFromPause = true
        updateTimer = false
    }



    fun testMethod(){
        Log.d("tag", "msg")
    }



}// class



