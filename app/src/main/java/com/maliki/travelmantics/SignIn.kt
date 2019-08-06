package com.maliki.travelmantics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.HashMap

class SignIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1
    private val firestore = FirebaseFirestore.getInstance()
    private val reference = firestore.collection("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        email_sign.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            Animatoo.animateSlideLeft(this)
        }

        google_sign.setOnClickListener { googleSignIn() }
    }
    private fun googleSignIn() {
        google_sign.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    val email = user.email
                    val name = user.displayName
                    val photo = user.photoUrl!!.toString()
                    val phone = user.phoneNumber

                    val newUserData = HashMap<String, Any>()
                    assert(name != null)
                    newUserData["name"] = name!!
                    newUserData["email"] = email!!
                    newUserData["photo"] = photo
                    //                            newUserData.put("phone", phone);

                    reference.document(user.uid).set(newUserData).addOnSuccessListener {
                        google_sign.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        startActivity(Intent(this, Decision::class.java))
                        Animatoo.animateSlideLeft(this)
                    }
                } else {
                    Toast.makeText(this@SignIn, "signInWithCredential:failure", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed " + e.message, Toast.LENGTH_SHORT).show()
                // ...
            }

        }
    }
}
