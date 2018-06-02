package com.example.nekuin.timer

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        when (position) {
/*
Fragment 1 = timer
Fragment 2 = säännöt
Fragment 3 = kartta
 */
            0 -> return Fragment2()
            1 -> return Fragment1()
            else -> return Fragment3()

        }
    }



    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(position)
    }

}