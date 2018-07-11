package fi.samuliraty.suurpeli.suurpelifirebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
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
    var testTime: Int = 5
    //while active handler
    val mHandler: Handler = Handler()
    //while paused handler
    val pHandler: Handler = Handler()
    //create a field for notification manager
    private var jManager: NotificationManager? = null

    //create a field for the notification
    private var jBuilder: NotificationCompat.Builder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left, container, false)
    }

    //create and add listener for timer value in the database
    private val valueListener = object :  ValueEventListener {
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

    override fun onResume() {
        super.onResume()

        //cancel notification when app is active
        jManager?.cancel(1)
        pHandler.removeCallbacks(updatePaused)

        //add listener for database value changes (also get the data for the first time)
        timerValue.addValueEventListener(valueListener)
        updateTimer.run()
        /*
        //build the notification
        jBuilder = NotificationCompat.Builder(activity?.baseContext!!, "notify_001")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Game time remaining:")
                .setContentText("" + increment)
                //.setStyle(object : NotificationCompat.BigTextStyle(){}.bigText("huutist\njoka\ntuutist\nkek"))
                .setPriority(1)
                //1 should be PUBLIC == show all the things in lock screen
                .setVisibility(1)
                .setOnlyAlertOnce(true)
                //should dismiss notification when user clicks/taps on it
                .setAutoCancel(true)
                */
    }

    //Create a runnable for updating the timer on screen
    private val updateTimer = object : Runnable {
        override fun run() {
            timeLeft--

            //if timer reaches 0 stop updating and update text
            if(timeLeft < 0){
                timerValueText.text = "no game"
                mHandler.removeCallbacks(this)
            }
            else
            {
                timerValueText.text = timeLeft.toString()
            }
            mHandler.postDelayed(this, 1000)
        }
    }

    //create a runnable for updating the timer when app is not active
    private val updatePaused: Runnable = object: Runnable {
        override fun run() {
            val delayMS: Long = 5000
            timeLeft -= delayMS/1000
            pHandler.postDelayed(this, delayMS)
            //update the contentText of the notification!!
            jBuilder?.setContentText("" + timeLeft)

            //if timer reaches 0 stop updating and dismiss notification
            if(timeLeft <= 0){
                jManager?.cancel(1)
                pHandler.removeCallbacks(this)
            }
            //push notification
            jManager?.notify(1, jBuilder?.build())
        }
    }


    override fun onPause() {
        super.onPause()
        //stop refreshing timer
        mHandler.removeCallbacks(updateTimer)
        //remove value listener so we don't crash the app if it changes while paused
        timerValue.removeEventListener(valueListener)
        updatePaused.run()
        //create notification manager
        jManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //create a notification
        jBuilder = NotificationCompat.Builder(activity?.baseContext!!, "notify_001")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Game time remaining:")
                .setContentText("" + timeLeft)
                //.setStyle(object : NotificationCompat.BigTextStyle(){}.bigText("huutist\njoka\ntuutist\nkek"))
                .setPriority(1)
                //1 should be PUBLIC == show all the things in lock screen
                .setVisibility(1)
                .setOnlyAlertOnce(true)
                //should dismiss notification when user clicks/taps on it
                .setAutoCancel(true)

        //create notification channel if using android oreo or higher
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channel: NotificationChannel = NotificationChannel("default", "jotain", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "channel desc"
            jManager?.createNotificationChannel(channel)
        }
        //push the notification
        jManager?.notify(1, jBuilder?.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        jManager?.cancel(1)
    }





}


