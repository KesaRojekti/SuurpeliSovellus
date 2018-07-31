package fi.suurpelisovellus.suurpeli.suurpelifirebase


import android.content.Context
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
    private val sDiag: SettingsDialogFragment = SettingsDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //create a sharedPreferences file if it doesn't exist already, and default notification setting to TRUE
        val sharedPref = getSharedPreferences(getString(R.string.settings_notifications_key), Context.MODE_PRIVATE)
        val sharedKey = getString(R.string.settings_notifications_key)
        if(!sharedPref.contains(sharedKey)){
            val editor = sharedPref?.edit()
            editor?.putBoolean(sharedKey, true)
            editor?.apply()
        }

        //get current user
        val user:FirebaseUser? = auth.currentUser

        //add listener to the user -> if you log out start login activity again
        val authListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(fbauth: FirebaseAuth) {
                val usr = fbauth.currentUser
                if (usr == null){
                    val loginIntent: Intent = Intent(this@MainActivity, SigninActivity::class.java)
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    //uncomment these if you are using login again
                    //startActivity(loginIntent)
                    //finish()
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
    }

    /**
     * Back button functions
     */
    override fun onBackPressed() {
        //comment the super call if you are using login system again
        //super.onBackPressed()

        //check if you are logged in or not
        //if you are not close the app
        //if you are move the app to the background for the notifications
        /* uncomment these if you are using login system again
        val mUser: FirebaseUser? = auth.currentUser
        if(mUser != null){
            moveTaskToBack(true)
        }
        */
        moveTaskToBack(true)

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

            val fm: android.app.FragmentManager? = fragmentManager
            sDiag.show(fm, "missiles")
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
                1 -> return FragmentMapEvent()
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
