package fi.suurpelisovellus.suurpeli.suurpelifirebase


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.jetbrains.anko.doAsync


/**
 * Schedule stuff
 *
 */
class ScheduleFragment : Fragment() {

    lateinit var schedules: Array<String>
    var i:Int = 0
    val handler: Handler = Handler()
    private var isOnPause: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onResume() {
        super.onResume()
        schedules = resources.getStringArray(R.array.aikataulu)
        scheduleText.text = schedules[i]

        scheduleButtonForward.setOnClickListener {
            pauseCallBacks()
            if(i >= schedules.size-1){
                i = 0
            }
            else
            {
                i++
            }
            scheduleText.text = schedules[i]
        }

        scheduleButtonBack.setOnClickListener {
            pauseCallBacks()
            if(i <= 0){
                i = schedules.size-1
            }
            else
            {
                i--
            }
            scheduleText.text = schedules[i]
        }

        handler.postDelayed(scheduleUpdater, 5000)
    }

    //rotate schedule every 3000 seconds
    private val scheduleUpdater = object : Runnable {
        override fun run() {
            if(!isOnPause){
                if(i >= schedules.size-1){
                    i = 0
                }
                else
                {
                    i++
                }
                scheduleText.text = schedules[i]
            }
            handler.postDelayed(this, 5000)
        }
    }

    private fun pauseCallBacks(){
        doAsync {
            isOnPause = true
            Thread.sleep(5000)
            isOnPause = false
        }
    }

    override fun onPause() {
        super.onPause()
        //remove updater so we don't crash
        handler.removeCallbacks(scheduleUpdater)
    }

}
