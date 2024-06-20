package cusat.ddukk.mfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username_register = findViewById<EditText>(R.id.username_reg)
        val email_register = findViewById<EditText>(R.id.email_reg)
        val password_register = findViewById<EditText>(R.id.password_reg)
        val confirm_p_register = findViewById<EditText>(R.id.confirm_pass_reg)
        val button_register = findViewById<Button>(R.id.button_reg)
        val button_logind = findViewById<Button>(R.id.button_logined)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        button_register.setOnClickListener {
            val reg_username = username_register.text.toString().trim()
            val reg_email = email_register.text.toString().trim()
            val reg_password = password_register.text.toString().trim()
            val confirm_password = confirm_p_register.text.toString().trim()

            if (reg_username.isNotEmpty() && reg_email.isNotEmpty() && reg_password.isNotEmpty() && confirm_password.isNotEmpty()) {
                if (reg_password == confirm_password) {
                    firebaseAuth.createUserWithEmailAndPassword(reg_email, reg_password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userDetails = hashMapOf(
                                "name" to reg_username,
                                "email" to reg_email
                            )

                            db.collection("users").document(firebaseAuth.currentUser!!.uid)
                                .set(userDetails)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "User Successfully Registered", Toast.LENGTH_SHORT).show()
                                    val i = Intent(this, LoginActivity::class.java)
                                    startActivity(i)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to Register User Details", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            task.exception?.let {
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        button_logind.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
    }
}
