package com.example.nekuin.timer

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment1.*
import kotlin.coroutines.experimental.Continuation


class MainActivity : AppCompatActivity() {


    internal var x = 0
    internal var timerValue = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PagerAdapter(supportFragmentManager) //getSupportFragmentManager
        val pager = findViewById<View>(R.id.pager) as ViewPager //vähän sama kun javassa luotaisiin olio AFAIK, ViewPager pager = new ViewPager(pager) tms

        pager.adapter = adapter //pager.setAdapter = adapter

        //make the tabs move
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs)) // tabs = tablayout ID, activity_main.xml
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(pager)) // pager = ViewPager ID, activity_main.xml

        pager.currentItem = 1 // "keski" fragment default viewiksi


    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

    }
}
