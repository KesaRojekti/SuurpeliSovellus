package fi.samuliraty.suurpeli.suurpelifirebase


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_child.*
import kotlinx.android.synthetic.main.fragment_left.*
import org.jetbrains.anko.doAsync


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * News fragments
 *
 */
class ChildFragment : Fragment() {

    private val newsHandler: Handler = Handler()
    private var index: Int = 0
    private var i: Int = 0
    private var isOnPause = false
    private lateinit var things: List<TextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_child, container, false)
    }

    private val newsUpdater: Runnable = object : Runnable {
        override fun run() {
            if(!isOnPause){
                i++
                if(i > 10){
                    index++
                    i = 0
                }
                if(index > 4){
                    index = 0
                }
            }
            when (index) {
                0 -> {
                    newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                    newsIndicators(index)
                }
                1 -> {
                    newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                    newsIndicators(index)
                }
                2 -> {
                    newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                    newsIndicators(index)
                }
                3 -> {
                    newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                    newsIndicators(index)
                }
                else -> {
                    newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                    newsIndicators(index)
                }
            }

            newsHandler.postDelayed(this, 100)
        }
    }


    //pause news update cycle if user clicks on back or forward
    private fun pauseCallbacks(){
        doAsync {
            isOnPause = true
            Thread.sleep(2500)
            isOnPause = false
        }
    }

    private fun newsIndicators(pos: Int){
        things = listOf(news1, news2, news3, news4, news5)
        for(n in things){
            n.alpha = 0.1f
        }
        when(pos){
            0 -> things[pos].alpha = 1f
            1 -> things[pos].alpha = 1f
            2 -> things[pos].alpha = 1f
            3 -> things[pos].alpha = 1f
            4 -> things[pos].alpha = 1f
        }
    }


    override fun onResume() {
        super.onResume()

        newsUpdater.run()
        buttonBack.setOnClickListener {
            index--
            if(index < 0){
                index = 4
            }
            pauseCallbacks()
        }

        buttonFwd.setOnClickListener {
            index++
            if(index > 4){
                index = 0
            }
            pauseCallbacks()
        }
    }

    override fun onStop() {
        super.onStop()
        newsHandler.removeCallbacks(newsUpdater)
    }



}
