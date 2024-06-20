package cusat.ddukk.mfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        var email_login = findViewById<EditText>(R.id.email_log)
        var password_login = findViewById<EditText>(R.id.password_log)
        var button_login = findViewById<Button>(R.id.button_log)

        var button_registered = findViewById<Button>(R.id.bt_delete_profile)

        firebaseAuth = FirebaseAuth.getInstance()

        button_login.setOnClickListener {
            var log_email= email_login.text.toString()
            var log_password = password_login.text.toString()

            if (log_email.isNotEmpty() && log_password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(log_email,log_password).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this,"You Are Successfully Logged", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    else{
                        Toast.makeText(this,"not successfull", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        button_registered.setOnClickListener {
            var i = Intent(applicationContext,MainActivity::class.java)
            startActivity(i)
        }
    }
}
