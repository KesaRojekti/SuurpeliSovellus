package com.example.emilpeli.saannot

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import java.util.ArrayList
import java.util.Collections

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // find list view. Basic stuff right here
        val listview: ListView
        listview = findViewById<View>(R.id.listView) as ListView

        // generate some data. basics naming. puts them in horizontal line
        val phones = arrayOf("Lääkintämiehet", "PST", "Rivimiehet", "Päällystö", "Tuomarit", "Kielletty pelaaminen", "Huivi", "Osuma", "Liput")

        // add data to ArrayList. resembles the java exercise i did couple of months ago
        val list = ArrayList<String>()
        Collections.addAll(list, *phones)


        // This was the first that displayed all logos the same. adapter and the layout is from rowlayout. text is from textView. Remember this one!!!!!!!
        // add data to ArrayAdapter (default Android ListView style/layout)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.textView, list);

        // create custom adapter for every logo there is a picture. good to remember
        val adapter = PhoneArrayAdapter(this, list)

        // set data to listView with adapter
        listview.adapter = adapter

        // item listener
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // get list row data (now String as a phone name)
            val phone = list[position]
            // create an explicit intent
            // add data to intent
            intent.putExtra("phone", phone)
            // start a new activity
            startActivity(intent)
        }

    }
}
