package com.example.nekuin.timer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.expandlist.*
import kotlinx.android.synthetic.main.fragment3.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment3 : Fragment() {

/*
    var listView:ExpandableListView = exLV
    var listDataHeader = ArrayList<String>()
    var listHash = HashMap<String, List<String>>()

    var listAdapter:ExpandableListAdapter = ExpandableListAdapter(this.context,listDataHeader,listHash)

*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        listView = exLV
        listAdapter = ExpandableListAdapter(this.context,listDataHeader,listHash)
        listView.setGroupIndicator(null)
        */
    }

    override fun onResume(){
        super.onResume()
/*
        initData()
        listView.setAdapter(listAdapter)
*/
    }
/*
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
*/
}