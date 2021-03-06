package fi.suurpelisovellus.suurpeli.suurpelifirebase

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
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
    private val lippuRef: DatabaseReference = database.getReference("lippu3")
    private val gamePauseRef = database.getReference("gamePaused")
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

    //track if the fragment is paused or not, used in notification handling, check valueListener onDataChange method
    private var isPaused: Boolean = false

    private var gamePaused: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left, container, false)
    }

    //create and add listener for timer value in the database
    /**
     * Create a listener to pull timer data from the database
     * If it detects a change in the value it gets pulled again
     */
    private val valueListener = object :  ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onDataChange(data: DataSnapshot) {
            val timer:Any? = data.value
            val time = (timer as Long).toLong()
            timeLeft = time - System.currentTimeMillis()
            timeLeft /= 1000 //time is now seconds
            //timerValueText.text = convertTime(timeLeft)

            //removeCallbacks is called in updatePaused runnable if timer reaches 0
            //this way, if the app is paused and we get a new timer it'll start updating the notification
            //without needing to open the app
            //isPaused changes in onResume and onPause methods
            if(isPaused){
                pHandler.removeCallbacks(updatePaused)
                updatePaused.run()
            }
        }
    }

    private val gamePauseListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(data: DataSnapshot) {
            val d: Any? = data.value
            val pause: Boolean = d as Boolean
            gamePaused = pause
            //Log.d("gamePause", "" + pause)
        }
    }


    override fun onResume() {
        super.onResume()
        //used in notification stuff, check valueListener onDataChange
        isPaused = false

        //cancel notification when app is active
        jManager?.cancel(TimerNotificationID)


        //remove pause handler callbacks
        pHandler.removeCallbacks(updatePaused)

        //add listener for database value changes (also get the data for the first time)
        timerValue.addValueEventListener(valueListener)

        //add listener to see if the game is paused or not
        gamePauseRef.addValueEventListener(gamePauseListener)

        //add news fragment
        childFragmentManager.beginTransaction().add(R.id.childView, ChildFragment()).commit()

        //add schedule fragment
        childFragmentManager.beginTransaction().add(R.id.scheduleView, ScheduleFragment()).commit()


        //start updating timer
        updateTimer.run()

    }


    /**
     * Runnable object to:
     *
     * Update the timer every 1000ms
     * Also converts the timer which is in milliseconds to (mm:ss) format
     *
     * If the timer reaches 0, stop updating
     *
     * called in onResume
     */
    private val updateTimer = object : Runnable {
        override fun run() {
            timeLeft--

            //if timer reaches 0 stop updating and update text
            if(timeLeft < 0){
                timerValueText.text = getString(R.string.no_game_text)
                mHandler.removeCallbacks(this)
            }
            //if the game is paused let users know, and don't update the timer
            else if(gamePaused){
                timerValueText.text = getString(R.string.game_paused_text)
            }
            else
            {
                timerValueText.text = convertTime(timeLeft)
            }
            mHandler.postDelayed(this, 1000)
        }
    }

    /**
     * Runnable object to:
     *
     * Push notifications every <delayMS> for the timer
     * timeLeft is in milliseconds, so we can subtract the <delayMS> from that
     * and we have the correct time in the notification.
     * Update the notifications ContentText
     * Called in onPause
     */
    private val updatePaused: Runnable = object: Runnable {
        override fun run() {
            val delayMS: Long = 10000
            // divided by 1000 because we want seconds, timeLeft is seconds by now
            timeLeft -= delayMS/1000
            //update the contentText of the notification!!
            pHandler.postDelayed(this, delayMS)
            jBuilder?.setContentText(convertTime(timeLeft))

            //if timer reaches 0 stop updating and dismiss notification
            if(timeLeft <= 0){
                pHandler.removeCallbacks(this)
                jManager?.cancel(TimerNotificationID)
            }
            if(gamePaused){
                jBuilder?.setContentText("Game is paused!")
                //push notification
                jManager?.notify(TimerNotificationID, jBuilder?.build())
            }
            else
            {
                //push notification
                jManager?.notify(TimerNotificationID, jBuilder?.build())
            }
        }
    }


    override fun onPause() {
        super.onPause()
        //used in notification stuff, check valueListener onDataChange
        isPaused = true
        //stop refreshing timer
        mHandler.removeCallbacks(updateTimer)

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
        jBuilder = NotificationCompat.Builder(activity?.baseContext!!, "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.gametime_notification_title))
                .setContentText(convertTime(timeLeft))
                //.setStyle(object : NotificationCompat.BigTextStyle(){}.bigText("huutist\njoka\ntuutist\nkek"))
                //old priority for older android versions, 0 should be default level
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

            val channel: NotificationChannel = NotificationChannel("default", "Game time while hidden", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Display timer while app is hidden"
            jManager?.createNotificationChannel(channel)
        }
    }

    override fun onStop(){
        super.onStop()
        isPaused = false
        //remove all callbacks
        pHandler.removeCallbacks(updatePaused)
        mHandler.removeCallbacks(updateTimer)
        //dismiss timer notification
        jManager?.cancel(TimerNotificationID)
    }

    override fun onDestroy() {
        super.onDestroy()
        isPaused = false
        //remove all callbacks
        pHandler.removeCallbacks(updatePaused)
        mHandler.removeCallbacks(updateTimer)
        //dismiss timer notification
        jManager?.cancel(TimerNotificationID)

    }

    /**
     * Convert seconds to a more traditional timer format (mm:ss)
     */
    fun convertTime(seconds: Long): String {
        val minutes = seconds / (60)
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }
}


