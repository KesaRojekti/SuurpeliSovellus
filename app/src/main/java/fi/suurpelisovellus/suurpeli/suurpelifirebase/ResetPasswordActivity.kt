package fi.suurpelisovellus.suurpeli.suurpelifirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)


        resetpwButton.setOnClickListener { _ ->
            val email:CharSequence = emailText.text
            if(email.isNotBlank()){
                val user = auth.sendPasswordResetEmail(email.toString())
                val resetlistener = OnCompleteListener<Void> { task ->
                    if(task.isSuccessful){
                        val intent: Intent = Intent(this@ResetPasswordActivity, SigninActivity::class.java)
                        Toast.makeText(this@ResetPasswordActivity, "Check your E-mail for instructions", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this@ResetPasswordActivity, "Invalid email address", Toast.LENGTH_SHORT).show()
                    }
                }
                user.addOnCompleteListener(resetlistener)
            }
            else
            {
                Toast.makeText(this@ResetPasswordActivity, "Enter a registered email", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener { _ ->
            val intent: Intent = Intent(this@ResetPasswordActivity, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
