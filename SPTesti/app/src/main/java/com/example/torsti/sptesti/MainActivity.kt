package com.example.torsti.sptesti

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {
    private var fragmentTransaction:FragmentTransaction = supportFragmentManager.beginTransaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        fragmentTransaction.replace(R.id.your_placeholder, FragmentMapEvent())
        fragmentTransaction.commit()
    }

}
