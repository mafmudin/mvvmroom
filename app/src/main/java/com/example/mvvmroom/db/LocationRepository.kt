package com.example.mvvmroom.db

import com.example.mvvmroom.model.LocationData

class LocationRepository(private val databaseDB: DatabaseDB) {
    suspend fun insert(locationData: LocationData) = databaseDB.getLocationDao().add(locationData)
    suspend fun update(locationData: LocationData) = databaseDB.getLocationDao().update(locationData)
    suspend fun delete(locationData: LocationData) = databaseDB.getLocationDao().delete(locationData)
    suspend fun get(page:Int) = databaseDB.getLocationDao().getAllLocation(page)
    suspend fun getActive(isActive:Int, page:Int) = databaseDB.getLocationDao().getActive(isActive, page)
}