package com.maliki.travelmantics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.HashMap

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private val reference = firestore.collection("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        sign_un.setOnClickListener { signUp() }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "SIgn Up"

    }
    private fun signUp() {
        //validate form
        val nameV = name.text.toString().trim()
        val emailV = email.text.toString().trim()
        val passwordV = password.text.toString().trim()
        if (nameV.isEmpty()||emailV.isEmpty()||passwordV.isEmpty()){
            password.error = "All fields are required"
            return
        }
        val userInfo = HashMap<String, Any>()
        userInfo["name"] = nameV
        userInfo["email"] = emailV
        val view = this@SignUp.currentFocus
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(emailV, passwordV)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user == null) {
                        Toast.makeText(this@SignUp, "Error", Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener

                    }
                    val profileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(nameV)
                        .build()
                    user.updateProfile(profileChangeRequest)
                    reference.document(user.uid)
                        .set(userInfo)
                        .addOnSuccessListener {
                            //success
                            startActivity(Intent(this, Decision::class.java))
                            Animatoo.animateSlideLeft(this)
                        }
                } else {
                    Snackbar.make(findViewById(R.id.top), "Authentication failed", Snackbar.LENGTH_LONG).show()
                }

            })

    }
}
