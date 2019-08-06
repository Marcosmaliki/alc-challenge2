package com.maliki.travelmantics

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_admin.*
import java.io.File
import java.util.*


class Admin : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var media: Uri? = null
    var returnValue: ArrayList<String> = ArrayList()
    private val reference = FirebaseFirestore.getInstance()
    private val collectionReference = reference.collection("Destinations")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        image.setOnClickListener {
            Pix.start(this, Options.init().setRequestCode(100))
//            TedRxImagePicker.with(this)
//                .mediaType(gun0912.tedimagepicker.builder.type.MediaType.IMAGE)
//                .startMultiImage()
//                .subscribe({ uriList ->
//                    media = uriList[0]
//                    image_preview.setImageURI(media)
//
//                }, Throwable::printStackTrace)
        }

        submit.setOnClickListener { uploadDestination() }


    }

    private fun uploadDestination() {
        val loc = location.text.toString().trim()
        val pr = price.text.toString().trim()
        val hot = hotel.text.toString().trim()
        if (loc.isEmpty() || pr.isEmpty() || hot.isEmpty() || media.toString().isEmpty()) {
            hotel.error = "All fields are required"
            return
        }
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        pbar.visibility = View.VISIBLE
        submit.visibility = View.GONE
        val file = Uri.fromFile(File(returnValue[0]))
        val storageReference = FirebaseStorage.getInstance().reference
        val ref = storageReference.child("destinations/" + file.lastPathSegment)
        val uploadTask = ref.putFile(file)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }

            // Continue with the task to get the download URL
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val destination = HashMap<String, Any>()
                destination["main_pic"] = downloadUri.toString()
                destination["location"] = loc
                destination["hotel"] = hot
                destination["price"] = pr

                collectionReference.add(destination).addOnSuccessListener {
                    pbar.visibility = View.GONE
                    submit.visibility = View.VISIBLE
                    Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_LONG).show()
                }

            } else {
                //failed
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            val imageUri = Uri.fromFile(File(returnValue[0]))
            image_preview.setImageURI(imageUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, Options.init().setRequestCode(100))
                } else {
                    Toast.makeText(this@Admin, "Approve permissions to select an image", Toast.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item!!.itemId) {
        R.id.save -> {
            uploadDestination()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
