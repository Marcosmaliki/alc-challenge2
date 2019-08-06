package com.maliki.travelmantics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.maliki.travelmantics.model.Hotel
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.item_.view.*

class User : AppCompatActivity() {
    private var adapter: FirestoreRecyclerAdapter<Hotel, UsersViewHolder>? = null
    private val reference = FirebaseFirestore.getInstance()
    private val collectionReference = reference.collection("Destinations")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        rv_hotels.layoutManager = LinearLayoutManager(this)
        loadUsers()
    }

    private fun loadUsers() {
        val query = collectionReference

        val response = FirestoreRecyclerOptions.Builder<Hotel>()
            .setQuery(query, Hotel::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<Hotel, UsersViewHolder>(response) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_, parent, false)
                return UsersViewHolder(view)
            }

            override fun onBindViewHolder(holder: UsersViewHolder, pos: Int, userObj: Hotel) {
//                val user = usersList[pos]
                val docRef = response.snapshots.getSnapshot(pos).id

                holder.location.text = userObj.location
                holder.hotel.text = userObj.hotel
                holder.price.text = userObj.price
                Glide.with(this@User)
                    .load(userObj.main_pic)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.itemView.image)

                holder.itemView.setOnClickListener {
                    Toast.makeText(this@User, docRef, Toast.LENGTH_SHORT).show()
                }
            }

        }
//        view.spin_kit_account.visibility = View.GONE
        rv_hotels.adapter = adapter
    }

    class UsersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var location: TextView = view.location
        var hotel: TextView = view.hotel
        var price: TextView = view.price
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }
}
