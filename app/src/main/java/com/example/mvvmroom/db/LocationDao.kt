package com.example.mvvmroom.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.mvvmroom.model.LocationData

@Dao
interface LocationDao {
    @Query("SELECT * FROM LocationData LIMIT 20 OFFSET :pageIndex")
    fun getAllLocation(pageIndex: Int): LiveData<MutableList<LocationData>>

    @Query("SELECT * FROM LocationData WHERE status= :isActive LIMIT 20 OFFSET :pageIndex")
    fun getActive(isActive: Int, pageIndex: Int): LiveData<MutableList<LocationData>>

    @Insert(onConflict = REPLACE)
    fun add(locationData: LocationData)

    @Update
    fun update(locationData: LocationData)

    @Delete
    fun delete(locationData: LocationData)
}