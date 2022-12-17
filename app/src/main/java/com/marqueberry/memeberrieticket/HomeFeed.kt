package com.marqueberry.memeberrieticket

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marqueberry.memeberrieticket.cache.OfflineStorage.homeFeedBack
import com.marqueberry.memeberrieticket.cache.OfflineStorage.isUserLoggedIn


class HomeFeed : Fragment() {
lateinit var sharedpref : SharedPreferences

private var layoutManager:RecyclerView.LayoutManager?=null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFeedBack=true
        isUserLoggedIn=true
        Log.i("TAGHomefeed", "onCreate: ")

        layoutManager = LinearLayoutManager(this.context)
        var recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview?.layoutManager= layoutManager
        adapter=RecyclerAdapter()

        if (recyclerview != null) {
            recyclerview.adapter=adapter
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onStop() {
        super.onStop()
        homeFeedBack=false
    }

    override fun onResume() {
        super.onResume()
        homeFeedBack=true
    }



}