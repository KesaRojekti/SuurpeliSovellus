package com.example.nekuin.timer

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.support.v7.app.AppCompatActivity


import android.widget.AdapterView
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment2.*

import java.util.ArrayList
import java.util.Collections

import java.util.*
import kotlin.coroutines.experimental.Continuation

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * SÄÄNNÖT screen
 */
class Fragment2 : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment2, container, false)


    }

    override fun onResume() {
        super.onResume()
        // find list view. Basic stuff right here
        val listview = listView

        // generate some data. basics naming. puts them in horizontal line
        val phones = arrayOf("Lääkintämiehet", "PST", "Rivimiehet", "Päällystö", "Tuomarit", "Kielletty pelaaminen", "Huivi", "Osuma", "Liput")

        // add data to ArrayList. resembles the java exercise i did couple of months ago
        val list = ArrayList<String>()
        Collections.addAll(list, *phones)


        // This was the first that displayed all logos the same. adapter and the layout is from rowlayout. text is from textView. Remember this one!!!!!!!
        // add data to ArrayAdapter (default Android ListView style/layout)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.textView, list);

        // create custom adapter for every logo there is a picture. good to remember
        val adapter = PhoneArrayAdapter(this.context, list)

        // set data to listView with adapter
        listview.adapter = adapter

        // item listener
        listview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // get list row data (now String as a phone name)
            val phone = list[position]
            // create an explicit intent
            // add data to intent
            val intent:Intent = Intent(this.context, DetailActivity::class.java)
            intent.putExtra("phone", phone)
            // start a new activity
            startActivity(intent)
        }

    }


}


