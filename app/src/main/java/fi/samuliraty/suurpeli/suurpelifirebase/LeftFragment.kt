package fi.samuliraty.suurpeli.suurpelifirebase

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.flags.impl.DataUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_left.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Timer related stuff
 */
class LeftFragment : Fragment() {

    //get databse instance and reference to a value
    val database = FirebaseDatabase.getInstance()
    val timerValue = database.getReference("targetTime")
    var timeLeft: Long = 0
    val mHandler: Handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left, container, false)
    }

    override fun onResume() {
        super.onResume()

        //create and add listener for timer value in the database
        val valueListener = object :  ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("LeftFragValueListener", "no go")
            }

            override fun onDataChange(data: DataSnapshot) {
                val timer:Any? = data.value
                val time = (timer as Long).toLong()
                timeLeft = time - System.currentTimeMillis()
                timeLeft /= 1000
                timerValueText.text = timeLeft.toString()
            }
        }
        timerValue.addValueEventListener(valueListener)
        updateTimer.run()

    }

    //Create a runnable for updating the timer on screen
    private val updateTimer = object : Runnable {
        override fun run() {
            timeLeft--

            if(timeLeft < 0){
                timerValueText.text = "no game"
            }
            else
            {
                timerValueText.text = timeLeft.toString()
            }

            mHandler.postDelayed(this, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(updateTimer)
    }





}


