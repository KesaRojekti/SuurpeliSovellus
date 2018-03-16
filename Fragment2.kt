package com.example.nekuin.timer

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment1.*
import kotlinx.android.synthetic.main.fragment2.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment2 : Fragment() {

    private var urlData: String = "" // work in progress variableja
    var loop: Boolean = false
    public var fetchedData = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment2, container, false)


    }

    override fun onResume(){
        super.onResume()
        FetchData().execute()


        val saannot = "ei saa juosta\nei saa ampua"
        saantoText.text=saannot

        pullDataButton.setOnClickListener{
            jsonView.text = FetchData().getData()
            Log.d("pullButton","kusipää: " + FetchData().getData())
        }

        val t = object : Thread() {
            override fun run() {

                while (!isInterrupted) {
                    try {
                        Thread.sleep(5000)
                        activity?.runOnUiThread(object : Runnable {
                            override fun run() {
                                Log.d("threads", "m: " + Fragment2().fetchedData)
                            }
                        })
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        t.start()
    }
    // -----tästä alaspäin erittäin work in progress-------- servulta datan pyytämis yrityksiä... saan sen ainoastaan näkymään Logissa jostain syystä
    //jos meinaatte itse kokeilla datan hakua, laittakaa AndroidManifest.xml:llään oikeudet <uses-permission android:name="android.permission.INTERNET"/>
    //jos haluatte jotain tiettyä kyseiselle servulle (rasperry pi) kysykää Nekuinilta discordissa
    fun setText(text: String){
        this.urlData = text
        this.fetchedData = text
        Log.d("Frag2.setText","Fetched data: " + this.fetchedData)
        println(text)

        /*
        try {
            jsonView.text = "asd"
            jsonView.text = this.urlData
        } catch (e: IllegalStateException){
            e.printStackTrace()
        }
        */
    }

    fun sendData(condition: Boolean){
        this.loop = condition
        Log.d("Frag2.sendData", "cond: " + this.loop)
    }


}

class FetchData : AsyncTask<String, String, String>() {

    var apiData = ""

    override fun doInBackground(vararg params: String?): String {


        //val apiResponse = URL("http://rframe.sytes.net:8080/test").readText()
        var apiResponse = URL("http://rframe.sytes.net:8080/numeroita.json").readText()
        this.apiData = apiResponse
        Log.d("api responded",this.apiData)

        //irrelevant
        return apiResponse
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.d("FetchData.OnPost", "data: " + this.apiData)
        Fragment2().setText(this.apiData)
        Fragment2().sendData(true)
        Fragment2().fetchedData = this.apiData
    }

    fun getData(): String{
        Log.d("Fetch.getData", "still here: " + this.apiData)
        return this.apiData
    }
}