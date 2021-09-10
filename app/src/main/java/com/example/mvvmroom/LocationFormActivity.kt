package com.example.mvvmroom

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.room.Database
import com.example.mvvmroom.base.BaseActivity
import com.example.mvvmroom.db.DatabaseDB
import com.example.mvvmroom.db.LocationRepository
import com.example.mvvmroom.model.Item
import com.example.mvvmroom.model.LocationData
import com.example.mvvmroom.networking.RetrofitInstance
import com.example.mvvmroom.viewmodels.LocationViewModelVactory
import com.example.mvvmroom.viewmodels.LocationViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_location_form.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.ln

class LocationFormActivity : BaseActivity(), OnMapReadyCallback{
    lateinit var map : GoogleMap
    private lateinit var viewModel: LocationViewModels
    private lateinit var databse: DatabaseDB
    private lateinit var repository: LocationRepository
    private lateinit var factory: LocationViewModelVactory
    lateinit var locationData: LocationData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_form)
        checkLocationPermission()

        val id = if (intent.hasExtra("location_data")) (intent.getSerializableExtra("location_data") as LocationData).id else null
        if (id == null){
            buttonDelete.visibility = View.GONE
        }else{
            locationData =  intent.getSerializableExtra("location_data") as LocationData
            buttonDelete.visibility = View.VISIBLE
            etName.setText(locationData.name!!)
            etAddress.setText(locationData.address!!)
            etCity.setText(locationData.city!!)
            etCode.setText(locationData.zipcode!!)

            lat = locationData.lat!!.toDouble()
            lng = locationData.lng!!.toDouble()

            swStatus.setStatus(locationData.status!!)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ivClose.setOnClickListener {
            finish()
        }

        transImage.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                val action = p1!!.action

                when(action){
                    MotionEvent.ACTION_UP ->{
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }

                    MotionEvent.ACTION_DOWN ->{
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }

                    MotionEvent.ACTION_MOVE ->{
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }
                }

                return true
            }

        })

        fabLocationPicker.setOnClickListener {
            getLocation(LatLng(lat, lng)){
                etAddress.setText(it.address.label)
            }
        }

        databse = DatabaseDB.getDatabase(this)
        repository = LocationRepository(databse)
        factory = LocationViewModelVactory(repository)
        viewModel = ViewModelProvider(this, factory)[LocationViewModels::class.java]

        buttonSave.setOnClickListener {
            if (id == null){
                insertData()
            }else{
                updateData(locationData.id!!)
            }
        }

        buttonDelete.setOnClickListener {
            deleteData(locationData)
        }
    }

    fun deleteData(locationdata: LocationData){
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.delete(locationdata).also {
                Toast.makeText(this@LocationFormActivity, "Berhasil Hapus data", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun insertData(){
        val name = etName.getText()
        val address = etAddress.getText()
        val city = etCity.getText()
        val code = etCode.getText()
        val status = swStatus.getStatus()

        val locationdata = LocationData(
            null,
            name,
            address,
            city,
            code,
            lat.toString(),
            lng.toString(),
            status
        )

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.insert(locationdata).also {
                Toast.makeText(this@LocationFormActivity, "Berhasil simpan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateData(id: Int){
        val name = etName.getText()
        val address = etAddress.getText()
        val city = etCity.getText()
        val code = etCode.getText()
        val status = swStatus.getStatus()

        val locationdata = LocationData(
            id,
            name,
            address,
            city,
            code,
            lat.toString(),
            lng.toString(),
            status
        )

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.update(locationdata).also {
                Toast.makeText(this@LocationFormActivity, "Berhasil Update", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private var hasFetch = false
    private val retrofitInstance = RetrofitInstance.create()
    private fun getLocation(latLng: LatLng, done: (Item) -> Unit) {
        val at = "${latLng.latitude},${latLng.longitude}"
        if (!hasFetch) {
            GlobalScope.launch {
                try {
                    val places = retrofitInstance.getLocation(at).items
                    runOnUiThread {
                        if (places.isNotEmpty()) {
                            done.invoke(places.first())
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    var lat = 0.0
    var lng = 0.0
    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
//        map.isMyLocationEnabled = true
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()

        val location: Location? =
            locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false)!!)

        Log.e("LATITUDE ", location!!.latitude.toString())
        if (location != null) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                    ), 13f
                )
            )
            val cameraPosition = CameraPosition.Builder()
                .target(
                    LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                    )
                ) // Sets the center of the map to location user
                .zoom(17f) // Sets the zoom
                .build() // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        map.setOnCameraIdleListener {
            lat = map.cameraPosition.target.latitude
            lng = map.cameraPosition.target.longitude
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (checkLocation()!!){

                        }else {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}