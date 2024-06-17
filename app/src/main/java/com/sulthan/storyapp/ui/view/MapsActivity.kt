package com.sulthan.storyapp.ui.view

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.sulthan.storyapp.R
import com.sulthan.storyapp.data.ResultState
import com.sulthan.storyapp.databinding.ActivityMapsBinding
import com.sulthan.storyapp.ui.helper.StoryViewModelFactory
import com.sulthan.storyapp.ui.viewmodel.StoryViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val boundsBuilder = LatLngBounds.Builder()

    private val storyViewModel by viewModels<StoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        storyViewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    Log.d("Stories With Location Success: ", result.data.listStory.toString())
                    result.data.listStory.forEach { data ->
                        val latLng = LatLng(data.lat.toDouble() , data.lon.toDouble())
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(data.name)
                                .snippet(data.description)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        )
                        boundsBuilder.include(latLng)
                    }
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.d("Stories with Location Error: ", result.error)
                }
            }
        }

        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->  {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}