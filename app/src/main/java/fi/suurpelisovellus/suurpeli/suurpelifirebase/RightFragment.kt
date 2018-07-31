package fi.suurpelisovellus.suurpeli.suurpelifirebase


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.fragment_right.*
import java.util.*
import kotlin.collections.LinkedHashMap


/**
 * Rule related stuff
 *
 */
class RightFragment : Fragment() {

    lateinit var parent: Array<String>
    lateinit var ohjeet: Array<String>
    lateinit var saannot: Array<String>
    lateinit var lippusiimat: Array<String>
    lateinit var tunnustavaria: Array<String>
    lateinit var eliminointisaannot: Array<String>
    lateinit var ajoneuvot: Array<String>
    lateinit var kilvet: Array<String>
    lateinit var pelinkulku: Array<String>
    lateinit var respat: Array<String>
    lateinit var mustajoukkue: Array<String>

    lateinit var ajoohje: Array<String>
    lateinit var pmrkanavat: Array<String>
    lateinit var aikataulu: Array<String>
    lateinit var sattuiko: Array<String>
    //lateinit var thirdLevelOhjeet: HashMap<String, Array<String>>
    //lateinit var thirdLevelSaannot: HashMap<String, Array<String>>
    var thirdLevelOhjeet: LinkedHashMap<String, Array<String>> = LinkedHashMap()
    var thirdLevelSaannot: LinkedHashMap<String, Array<String>> = LinkedHashMap()

    lateinit var secondLevel: List<Array<String>>
    var data: List<LinkedHashMap<String, Array<String>>> = ArrayList()




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initLists()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_right, container, false)
    }

    override fun onResume() {
        super.onResume()
        //set up lists
        secondLevel = listOf(ohjeet, saannot)

        thirdLevelOhjeet[ohjeet[0]] = ajoohje
        thirdLevelOhjeet[ohjeet[1]] = pmrkanavat
        thirdLevelOhjeet[ohjeet[2]] = aikataulu
        thirdLevelOhjeet[ohjeet[3]] = sattuiko

        thirdLevelSaannot[saannot[0]] = lippusiimat
        thirdLevelSaannot[saannot[1]] = tunnustavaria
        thirdLevelSaannot[saannot[2]] = eliminointisaannot
        thirdLevelSaannot[saannot[3]] = ajoneuvot
        thirdLevelSaannot[saannot[4]] = kilvet
        thirdLevelSaannot[saannot[5]] = pelinkulku
        thirdLevelSaannot[saannot[6]] = respat
        thirdLevelSaannot[saannot[7]] = mustajoukkue

        data = listOf(thirdLevelOhjeet, thirdLevelSaannot)

        val expListView: ExpandableListView = lvExp

        val threeLevelListAdapterAdapter = ThreeLevelListAdapter(activity?.baseContext, parent, secondLevel, data)

        expListView.setAdapter(threeLevelListAdapterAdapter)
    }

    /**
     * Populate lists with resource arrays
     */
    private fun initLists(){
        parent = resources.getStringArray(R.array.ohjeetsaannot)
        ohjeet = resources.getStringArray(R.array.ohjeet)
        saannot = resources.getStringArray(R.array.saannot)

        lippusiimat = resources.getStringArray(R.array.lippusiimat)
        tunnustavaria = resources.getStringArray(R.array.tunnustavaria)
        eliminointisaannot = resources.getStringArray(R.array.eliminointisaannot)
        ajoneuvot = resources.getStringArray(R.array.ajoneuvot)
        kilvet = resources.getStringArray(R.array.kilvet)
        pelinkulku = resources.getStringArray(R.array.pelinkulku)
        respat = resources.getStringArray(R.array.respat)
        mustajoukkue = resources.getStringArray(R.array.mustajoukkue)

        ajoohje = resources.getStringArray(R.array.ajoohje)
        pmrkanavat = resources.getStringArray(R.array.pmrkanavat)
        aikataulu = resources.getStringArray(R.array.aikataulu)
        sattuiko = resources.getStringArray(R.array.sattuiko)
    }

}
