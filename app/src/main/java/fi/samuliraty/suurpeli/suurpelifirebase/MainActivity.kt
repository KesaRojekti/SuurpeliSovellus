package fi.samuliraty.suurpeli.suurpelifirebase

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.constraint.Placeholder
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private val auth:FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //get current user
        val user:FirebaseUser? = auth.currentUser
        Log.d("MainActivity user", "" + user)

        //add listener to the user -> if you log out start login activity again
        val authListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(fbauth: FirebaseAuth) {
                val usr = fbauth.currentUser
                if (usr == null){
                    val loginIntent: Intent = Intent(this@MainActivity, SigninActivity::class.java)
                    startActivity(loginIntent)
                    finish()
                }
            }

        }


        //look for changes in the current user
        auth.addAuthStateListener(authListener)



        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))


        val database = FirebaseDatabase.getInstance()
        //getReference = taulun nimi
        val myRef = database.getReference("message")
        val timerValue = database.getReference("targetTime")

        //write text into the database
        changeTextButton.setOnClickListener { _ ->
            myRef.setValue("hello")
        }

        //write different text into the database
        timerButton.setOnClickListener { _ ->
            myRef.setValue("world")
            //current time +90 mins
            timerValue.setValue(System.currentTimeMillis()+5400000)
        }


        //check for changes in the realtime database
        val listener = object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val value = data.value
                dbText.text = "" + value
            }

            override fun onCancelled(data: DatabaseError) {
                Log.d("listener", "failed")
            }

        }

        //add value change listener
        myRef.addValueEventListener(listener)

    }


    override fun onResume() {
        super.onResume()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser
        if(user != null){
            Toast.makeText(this, "logged in as: " + user.email, Toast.LENGTH_LONG).show()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            Toast.makeText(this, "super awesome settings", Toast.LENGTH_SHORT).show()
            return true
        }
        if(id == R.id.action_logout){
            auth.signOut()
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            when (position){
                0 -> return LeftFragment()
                1 -> return MiddleFragment()
                else -> return RightFragment()
            }
            //return PlaceholderFragment.newInstance(position)

        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }


    }



}
