package fi.samuliraty.suurpeli.suurpelifirebase

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_left.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Timer related stuff
 */
class LeftFragment : Fragment() {

    //get databse instance and reference to a value
    private val database = FirebaseDatabase.getInstance()
    private val timerValue = database.getReference("targetTime")
    private var timeLeft: Long = 0
    //while active handler
    private val mHandler: Handler = Handler()
    //while paused handler
    private val pHandler: Handler = Handler()
    //create a field for notification manager
    private var jManager: NotificationManager? = null
    //create id for the timer notification
    private val TimerNotificationID: Int = 1

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
            //timerValueText.text = convertTime(timeLeft)
        }
    }



    override fun onResume() {
        super.onResume()

        //cancel notification when app is active
        jManager?.cancel(TimerNotificationID)
        //remove pause handler callbacks
        pHandler.removeCallbacks(updatePaused)

        //add listener for database value changes (also get the data for the first time)
        timerValue.addValueEventListener(valueListener)

        //add news fragment
        childFragmentManager.beginTransaction().add(R.id.childView, ChildFragment()).commit()


        //start updating timer
        updateTimer.run()

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
                timerValueText.text = convertTime(timeLeft)
            }
            mHandler.postDelayed(this, 1000)
        }
    }

    //create a runnable for updating the timer when app is not active
    private val updatePaused: Runnable = object: Runnable {
        override fun run() {
            val delayMS: Long = 1000
            timeLeft -= delayMS/1000
            pHandler.postDelayed(this, delayMS)
            //update the contentText of the notification!!
            jBuilder?.setContentText(convertTime(timeLeft))

            //if timer reaches 0 stop updating and dismiss notification
            if(timeLeft <= 0){
                jManager?.cancel(TimerNotificationID)
                pHandler.removeCallbacks(this)
            }
            //push notification
            jManager?.notify(TimerNotificationID, jBuilder?.build())
        }
    }


    override fun onPause() {
        super.onPause()
        //stop refreshing timer
        mHandler.removeCallbacks(updateTimer)
        //remove value listener so we don't crash the app if it changes while paused
        timerValue.removeEventListener(valueListener)



        //settings stuff
        val sharedPref = activity?.getSharedPreferences(getString(R.string.settings_notifications_key), Context.MODE_PRIVATE)
        val sharedKey = getString(R.string.settings_notifications_key)
        val def = false
        val showNotifications = sharedPref?.getBoolean(sharedKey, def)

        //set up a "return intent", use this to get back to the app from the notification
        val returnIntent: Intent = Intent(activity?.baseContext, MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(activity?.baseContext)
        stackBuilder.addNextIntentWithParentStack(returnIntent)
        val returnPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        //start pushing notification if necessary
        if(timeLeft > 0 && showNotifications!!){
            updatePaused.run()
        }
        //create notification manager
        jManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //create a notification
        jBuilder = NotificationCompat.Builder(activity?.baseContext!!, "notify_001")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Game time remaining:")
                .setContentText(convertTime(timeLeft))
                //.setStyle(object : NotificationCompat.BigTextStyle(){}.bigText("huutist\njoka\ntuutist\nkek"))
                .setPriority(0)
                //1 should be PUBLIC == show all the things in lock screen
                .setVisibility(1)
                .setOnlyAlertOnce(true)
                //should dismiss notification when user clicks/taps on it
                .setAutoCancel(true)
                //set the intent which fires when you click the notification
                .setContentIntent(returnPendingIntent)


        //create notification channel if using android oreo or higher
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channel: NotificationChannel = NotificationChannel("default", "timerNotification", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "channel desc"
            jManager?.createNotificationChannel(channel)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //remove all callbacks
        pHandler.removeCallbacks(updatePaused)
        mHandler.removeCallbacks(updateTimer)
        //dismiss timer notification
        jManager?.cancel(TimerNotificationID)
    }

    //convert time to mm:ss format
    fun convertTime(seconds: Long): String {
        val minutes = seconds / (60)
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }







}


