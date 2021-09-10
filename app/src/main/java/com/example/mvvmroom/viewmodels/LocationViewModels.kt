package com.example.mvvmroom.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmroom.db.LocationRepository
import com.example.mvvmroom.model.LocationData

class LocationViewModels(private val repository: LocationRepository) : ViewModel(){
    suspend fun insert(locationData: LocationData) = repository.insert(locationData)
    suspend fun update(locationData: LocationData) = repository.update(locationData)
    suspend fun delete(locationData: LocationData) = repository.delete(locationData)
    suspend fun get(page:Int) = repository.get(page)
    suspend fun getActive(isActive: Int, page:Int) = repository.getActive(isActive, page)
}