package com.sulthan.storyapp.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sulthan.storyapp.data.ResultState
import com.sulthan.storyapp.databinding.ActivityAddStoryBinding
import com.sulthan.storyapp.ui.helper.StoryViewModelFactory
import com.sulthan.storyapp.ui.helper.getImageUri
import com.sulthan.storyapp.ui.helper.reduceFileImage
import com.sulthan.storyapp.ui.helper.uriToFile
import com.sulthan.storyapp.ui.viewmodel.StoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var myLocation: MutableMap<String, Float> = mutableMapOf()
    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<StoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.uploadButton.setOnClickListener {
            uploadStory()
        }

        getMyLocation()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSucces ->
            if (isSucces) {
                showImage()
            }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImage.setImageURI(it)
        }
    }

    private fun uploadStory() {

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descriptionEditText.text.toString()
            showLoading(true)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            var lat = if (binding.locationCheck.isChecked && myLocation.isNotEmpty()) myLocation["lat"] else null
            var lon = if (binding.locationCheck.isChecked && myLocation.isNotEmpty()) myLocation["lon"] else null

            viewModel.uploadStory(requestBody, multipartBody, lat, lon).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        showLoading(false)
                        Log.d("Add Story Success: ", result.data.toString())
                        showToast(result.data.message.toString())
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or    Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        Log.e("Add Story Error: ", result.error)
                        showToast(result.error)
                    }
                }
            }
        } ?: showToast("Image is empty")
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { loc ->
                val latitude = loc.latitude.toFloat()
                val longitude = loc.longitude.toFloat()
                myLocation.put("lat", latitude)
                myLocation.put("lon", longitude)
                Log.d("My Location", myLocation.toString())
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressIndicator.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}