package fi.samuliraty.suurpeli.suurpelifirebase


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.fragment_right.*


/**
 * Rule related stuff
 *
 */
class RightFragment : Fragment() {

    var listDataHeader: List<String> = listOf("PST-pelaaja", "Lääkintämies", "Ryhmänjohtaja", "Sektorinjohtaja", "Komentaja")
    var listHash: HashMap<String, List<String>>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_right, container, false)
    }

    override fun onResume() {
        super.onResume()
        val listView = lvExp as ExpandableListView
        //populate lists
        initData()
        val listAdapter = ExpandableListAdapter(activity?.baseContext, listDataHeader, listHash)
        listView.setAdapter(listAdapter)
    }

    /**
     * Populate the listDataHeader and listHash with data
     */
    private fun initData(){
        listHash = HashMap<String, List<String>>()

        val pstList: List<String> = listOf(getString(R.string.pst_rule))
        val medicList: List<String> = listOf(getString(R.string.medic_rule))
        val ryhmList: List<String> = listOf(getString(R.string.ryhm_rule))
        val sektoriList: List<String> = listOf(getString(R.string.sektori_rule))
        val komentajaList: List<String> = listOf(getString(R.string.komentaja_rule))
        listHash?.put(listDataHeader.get(index = 0), pstList)
        listHash?.put(listDataHeader.get(index = 1), medicList)
        listHash?.put(listDataHeader.get(index = 2), ryhmList)
        listHash?.put(listDataHeader.get(index = 3), sektoriList)
        listHash?.put(listDataHeader.get(index = 4), komentajaList)


    }

}
