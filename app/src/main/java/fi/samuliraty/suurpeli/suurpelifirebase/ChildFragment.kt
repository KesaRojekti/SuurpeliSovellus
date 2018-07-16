package fi.samuliraty.suurpeli.suurpelifirebase


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_child.*
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
    private lateinit var v: View
    private lateinit var textView: TextView

    //transition stuff
    private var textHasChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        /*
        v = inflater.inflate(R.layout.fragment_child, container, false)
        val transitionContainer: ViewGroup = v.findViewById(R.id.transitions_container)
        textView = transitionContainer.findViewById(R.id.transitionText)


        return v
        */
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_child, container, false)
    }



    //create a runnable which updates the news every X milliseconds
    private val newsUpdater: Runnable = object : Runnable {
        override fun run() {

            //pause = when user clicks on the news buttons
            if(!isOnPause){
                //counter within the runnable, every 10 runs increment index which changes the news
                //by using the counter we can have better response time to user inputs, i.e if you press
                //back it only takes 100ms to update the news, instead of the full 1 second
                i++
                if(i > 10){
                    index++
                    //textHasChanged = true
                    //newsText.startAnimation(anim)
                    i = 0
                }
                if(index > 4){
                    index = 0
                }
            }

            when (index) {
                /*
                0 -> {
                    if(textHasChanged) {
                        TransitionManager.beginDelayedTransition(transitions_container, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                        //newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        transitionText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        newsIndicators(index)
                        textHasChanged = false
                    }
                }
                1 -> {
                    if(textHasChanged) {
                        TransitionManager.beginDelayedTransition(transitions_container, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                        //newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        transitionText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        newsIndicators(index)
                        textHasChanged = false
                    }
                }
                2 -> {
                    if(textHasChanged) {
                        TransitionManager.beginDelayedTransition(transitions_container, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                        //newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        transitionText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        newsIndicators(index)
                        textHasChanged = false
                    }
                }
                3 -> {
                    if(textHasChanged) {
                        TransitionManager.beginDelayedTransition(transitions_container, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                        //newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        transitionText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        newsIndicators(index)
                        textHasChanged = false
                    }
                }
                else -> {
                    if(textHasChanged) {
                        TransitionManager.beginDelayedTransition(transitions_container, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                        //newsText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        transitionText.text = "news: " + index + " is awesome, but not that awesome tho it's still pretty awesome so in conclusion it's awesome"
                        newsIndicators(index)
                        textHasChanged = false
                    }
                }
                */
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

            //run every 100ms
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

    //update the circle indicators under the news text
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

    //@AddTrace(name = "childFragment", enabled = true)
    override fun onResume() {
        super.onResume()
        //start cycling the news
        newsUpdater.run()

        //add listeners to back and forward buttons
        //you can manually change the news with these
        buttonBack.setOnClickListener {
            index--
            //textHasChanged = true
            if(index < 0){
                index = 4
            }
            pauseCallbacks()
        }

        buttonFwd.setOnClickListener {
            index++
            //textHasChanged = true
            if(index > 4){
                index = 0
            }
            pauseCallbacks()
        }
    }

    override fun onStop() {
        super.onStop()
        //stop cycling the news when the fragment is not visible
        newsHandler.removeCallbacks(newsUpdater)
    }



}
