package com.sunnyweather.android.ui.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.FragmentPlaceBinding

class PlaceFragment: Fragment() {

    private lateinit var fragmentPlaceBinding: FragmentPlaceBinding

    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPlaceBinding= FragmentPlaceBinding.inflate(inflater,container,false)
//        return inflater.inflate(R.layout.fragment_place,container,false)
        return fragmentPlaceBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager= LinearLayoutManager(activity)
        fragmentPlaceBinding.recyclerView.layoutManager=layoutManager
        adapter= PlaceAdapter(this,viewModel.placeList)
        fragmentPlaceBinding.recyclerView.adapter=adapter
        fragmentPlaceBinding.searchPlaceEdit.addTextChangedListener { editable ->
            val content=editable.toString()
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }else{
                fragmentPlaceBinding.recyclerView.visibility=View.GONE
                fragmentPlaceBinding.bgImageView.visibility=View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places=result.getOrNull()
            if(places!=null){
                fragmentPlaceBinding.recyclerView.visibility=View.VISIBLE
                fragmentPlaceBinding.bgImageView.visibility=View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else{
                Toast.makeText(activity,"未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}