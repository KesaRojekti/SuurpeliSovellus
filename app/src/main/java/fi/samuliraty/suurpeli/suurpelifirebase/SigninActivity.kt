package fi.samuliraty.suurpeli.suurpelifirebase

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_signin.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap


class SigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)


        usernameText.requestFocus()


        val auth:FirebaseAuth = FirebaseAuth.getInstance()
        //get current user
        val user: FirebaseUser? = auth.currentUser

        //create a sharedPreferences file if it doesn't exist already, and default notification setting to TRUE
        val sharedPref = getSharedPreferences(getString(R.string.settings_notifications_key), Context.MODE_PRIVATE)
        val sharedKey = getString(R.string.settings_notifications_key)
        if(!sharedPref.contains(sharedKey)){
            val editor = sharedPref?.edit()
            editor?.putBoolean(sharedKey, true)
            editor?.apply()
        }

        //if you are already logged in skip the login activity
        if(user != null){
            val startMain: Intent = Intent(this, MainActivity::class.java)
            startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(startMain)
            finish()
        }



        //sign in button listener
        intentButton.setOnClickListener { _ ->
            //create intent for MainActivity, extra not used atm
            val startMain: Intent = Intent(this, MainActivity::class.java).apply {
                putExtra("LOGIN", "success")
            }
            if(usernameText.text.isNotBlank() && passwordText.text.isNotBlank()) {


                //get text from email and password fields
                val username: CharSequence = usernameText.text
                val password: CharSequence = passwordText.text

                //create auth and user
                val intentauth: FirebaseAuth = FirebaseAuth.getInstance()
                val intentuser = intentauth.signInWithEmailAndPassword(username.toString(), password.toString())

                //create complete listener, only start mainactivity if login is successful
                val completeListener = OnCompleteListener<AuthResult> { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this@SigninActivity, "can't login", Toast.LENGTH_SHORT).show()
                    } else {
                        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(startMain)
                        finish()
                    }
                }
                //add complete listener to user
                intentuser.addOnCompleteListener(this, completeListener)
            }
            else
            {
                Toast.makeText(this@SigninActivity, "Something is missing..", Toast.LENGTH_SHORT).show()
            }
        }

        //start new account activity
        newAccountButton.setOnClickListener { _ ->
            val startSignUp: Intent = Intent(this, SignupActivity::class.java)
            startActivity(startSignUp)
            finish()
        }

        signAnon.setOnClickListener { _ ->
            val signIn = auth.signInWithEmailAndPassword("anon@gmail.com", "morohoro")
            val startMain: Intent = Intent(this, MainActivity::class.java)
            val anonListener = OnCompleteListener<AuthResult> { task ->
                if(task.isSuccessful){
                    startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(startMain)
                    finish()
                }
            }
            signIn.addOnCompleteListener(anonListener)
        }

        forgotPassButton.setOnClickListener { _ ->
            val startForgotPw: Intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(startForgotPw)
            finish()
        }
    }

    override fun onStop(){
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val restart: Intent = Intent(this@SigninActivity, MainActivity::class.java)
    }
}
