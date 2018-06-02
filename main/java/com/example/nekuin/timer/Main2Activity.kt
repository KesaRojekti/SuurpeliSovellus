package com.example.nekuin.timer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.expandlist.*


class Main2Activity : AppCompatActivity() {

    internal var x = Int
    var listView: ExpandableListView = exLV
    var listDataHeader = ArrayList<String>()
    var listHash = HashMap<String, List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        textView.text = "Hello world!"



        val listAdapter:ExpandableListAdapter = ExpandableListAdapter(this,listDataHeader,listHash)

        listView.setAdapter(listAdapter)

    }

    fun initData(){
        try {
            listDataHeader.add("Sääntö1")
            listDataHeader.add("Sääntö2")
            listDataHeader.add("Sääntö3")
            listDataHeader.add("Sääntö4")
        } catch (e: NullPointerException){
            e.printStackTrace()
        }


        val rule1 = listOf("Sääntö1.1", "Sääntö1.2", "Sääntö1.3")
        val rule2 = listOf("Sääntö2.1", "Sääntö2.2", "Sääntö2.3")
        val rule3 = listOf("Sääntö3.1", "Sääntö3.2", "Sääntö3.3")
        val rule4 = listOf("Sääntö4.1", "Sääntö4.2", "Sääntö4.3")


        listHash.put(listDataHeader.get(0), rule1)
        listHash.put(listDataHeader.get(1), rule2)
        listHash.put(listDataHeader.get(2), rule3)
        listHash.put(listDataHeader.get(3), rule4)
    }
}
