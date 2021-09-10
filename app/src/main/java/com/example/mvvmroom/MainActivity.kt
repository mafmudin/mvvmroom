package com.example.mvvmroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmroom.adapter.LocationAdapter
import com.example.mvvmroom.db.DatabaseDB
import com.example.mvvmroom.db.LocationRepository
import com.example.mvvmroom.model.LocationData
import com.example.mvvmroom.viewmodels.LocationViewModelVactory
import com.example.mvvmroom.viewmodels.LocationViewModels
import kotlinx.android.synthetic.main.activity_main_drawer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), LocationAdapter.LocationAdapterListener{
    private lateinit var viewModel: LocationViewModels
    private lateinit var databse: DatabaseDB
    private lateinit var repository: LocationRepository
    private lateinit var factory: LocationViewModelVactory


    lateinit var adapter : LocationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_drawer)

        fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, LocationFormActivity::class.java)
            startActivity(intent)
        }

        adapter = LocationAdapter(this)
        rvLocation.adapter = adapter

        databse = DatabaseDB.getDatabase(this)
        repository = LocationRepository(databse)
        factory = LocationViewModelVactory(repository)
        viewModel = ViewModelProvider(this, factory)[LocationViewModels::class.java]

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.get(1).observe(this@MainActivity, {
                adapter.updateList(it)
                tvStatus.setText(String.format("All Status (%s)", it.size))
                var active = 0
                var inactive = 0
                for (data in it){
                    if (data.status == 1){
                        active++
                    }else{
                        inactive++
                    }
                }
                tvStatusActive.setText(String.format("Active (%s)", active))
                tvStatusInactive.setText(String.format("Inactive (%s)", inactive))
            })
        }

        tvStatus.setOnClickListener {
            switch(tvStatus, tvStatusActive, tvStatusInactive)

            CoroutineScope(Dispatchers.Main).launch {
                viewModel.get(1).observe(this@MainActivity, {
                    adapter.updateList(it)
                })
            }
        }

        tvStatusActive.setOnClickListener {
            switch(tvStatusActive, tvStatus, tvStatusInactive)
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.getActive(1,1).observe(this@MainActivity, {
                    adapter.updateList(it)
                })
            }
        }

        tvStatusInactive.setOnClickListener {
            switch(tvStatusInactive, tvStatus, tvStatusActive)

            CoroutineScope(Dispatchers.Main).launch {
                viewModel.getActive(2,1).observe(this@MainActivity, {
                    adapter.updateList(it)
                })
            }
        }
    }

    override fun onClickList(data: LocationData, position: Int) {
        val intent = Intent(this@MainActivity, LocationFormActivity::class.java)
        intent.putExtra("location_data",data)
        startActivity(intent)
    }

    fun switch(active: TextView, tv1: TextView, tv2:TextView){
        active.setTextColor(ContextCompat.getColor(this, R.color.green))
        active.setBackgroundResource(R.color.lighter_green)

        tv1.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        tv1.setBackgroundResource(R.color.gray)

        tv2.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        tv2.setBackgroundResource(R.color.gray)
    }
}