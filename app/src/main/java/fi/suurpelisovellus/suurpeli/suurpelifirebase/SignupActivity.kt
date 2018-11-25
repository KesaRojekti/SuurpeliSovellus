package fi.suurpelisovellus.suurpeli.suurpelifirebase

import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        //register button listener
        registerButton.setOnClickListener { _ ->

            //check for blank fields, both have to contain something
            if (userNameText.text.isNotBlank() && passwordText.text.isNotBlank()) {


                //get input text from email and password fields
                val email: CharSequence = userNameText.text
                val password: CharSequence = passwordText.text

                //create user variable
                val user = auth.createUserWithEmailAndPassword(email.toString(), password.toString())

                //create complete listener for user
                val cmpListener = OnCompleteListener<AuthResult> { task ->
                    //start sign in activity if you successfully created a user
                    //also seems to sign you in automagically
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "ei onnaa", Toast.LENGTH_SHORT).show()
                    } else {
                        val signinIntent: Intent = Intent(this@SignupActivity, SigninActivity::class.java)
                        signinIntent.putExtra("EMAIL", email.toString())
                        startActivity(signinIntent)
                        finish()
                    }
                }
                //add complete listener to user
                user.addOnCompleteListener(this, cmpListener)

            }
            else
            {
                Toast.makeText(this@SignupActivity, "Something is missing..", Toast.LENGTH_SHORT).show()
            }
        }

        //backbutton listener
        backButton.setOnClickListener { _ ->
            val signinIntent: Intent = Intent(this@SignupActivity, SigninActivity::class.java)
            startActivity(signinIntent)
            finish()
        }
    }
}
