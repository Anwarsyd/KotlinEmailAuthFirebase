package cusat.ddukk.mfirebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var profileImage: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var uploadImageButton: Button
    private lateinit var saveDetailsButton: Button
    private var imageUrl: Uri? = null

    private lateinit var chooseImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val usernameProfile = findViewById<TextView>(R.id.prof_name_textview)
        val emailProfile = findViewById<TextView>(R.id.prof_email_textview)
        val deleteProfileButton = findViewById<Button>(R.id.bt_delete_profile)
        profileImage = findViewById(R.id.profile_image)
        chooseImageButton = findViewById(R.id.button_choose_image)
        uploadImageButton = findViewById(R.id.button_upload_image)
        saveDetailsButton = findViewById(R.id.button_save_details)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val imageUrl = document.getString("imageUrl")

                        usernameProfile.text = name
                        emailProfile.text = email
                        if (imageUrl != null && imageUrl.isNotEmpty()) {
                            profileImage.setImageURI(Uri.parse(imageUrl))
                        }
                    } else {
                        usernameProfile.text = "No such document"
                        emailProfile.text = ""
                    }
                }
                .addOnFailureListener { exception ->
                    usernameProfile.text = "Error: ${exception.message}"
                    emailProfile.text = ""
                }
        }

        chooseImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.data != null) {
                imageUrl = result.data!!.data
                profileImage.setImageURI(imageUrl)
            }
        }

        chooseImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            chooseImageLauncher.launch(intent)
        }

        uploadImageButton.setOnClickListener {
            if (imageUrl != null) {
                val fileReference = storageRef.child("profile_images/${firebaseAuth.currentUser?.uid}.jpg")
                fileReference.putFile(imageUrl!!)
                    .addOnSuccessListener { taskSnapshot ->
                        fileReference.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri
                            Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        saveDetailsButton.setOnClickListener {
            val user = firebaseAuth.currentUser
            if (user != null && imageUrl != null) {
                db.collection("users").document(user.uid)
                    .update("imageUrl", imageUrl.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Image URL Saved Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save image URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show()
            }
        }

        deleteProfileButton.setOnClickListener {
            currentUser?.let { user ->
                db.collection("users").document(user.uid).delete()
                    .addOnSuccessListener {
                        user.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Profile Deleted", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to delete profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
