package com.example.mvvmroom.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmroom.db.LocationRepository
import java.lang.Exception

class LocationViewModelVactory(private val repository: LocationRepository)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        try {
            val cons = modelClass.getDeclaredConstructor(LocationRepository::class.java)
            return  cons.newInstance(repository)
        }catch (e:Exception){
            e.printStackTrace()
        }

        return super.create(modelClass)
    }
}