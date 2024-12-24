package com.submissionandroid.storyapp.view.add_story

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.submissionandroid.storyapp.data.AddNewStoryResponse
import com.submissionandroid.storyapp.data.StoryRepository
import com.submissionandroid.storyapp.data.pref.UserPreference
import com.submissionandroid.storyapp.data.pref.dataStore
import com.submissionandroid.storyapp.databinding.ActivityAddStoryBinding
import com.submissionandroid.storyapp.di.Injection
import com.submissionandroid.storyapp.utils.FileHelper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var selectedImageFile: File? = null
    private lateinit var storyRepository: StoryRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val userPreference = UserPreference.getInstance(dataStore)
        storyRepository = Injection.provideStoryRepository(this)

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }


        setupListeners()
    }

    private fun setupListeners() {
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.btnCamera.setOnClickListener { openCamera() }
        binding.btnUpload.setOnClickListener { uploadStory() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getCurrentLocation()
            } else {
                currentLocation = null
            }
        }

    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageFile = FileHelper.uriToFile(it, this)
                Glide.with(this).load(it).into(binding.imagePreview)
            }
        }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                val uri = FileHelper.saveBitmapToFile(it, this)
                selectedImageFile = FileHelper.uriToFile(uri, this)
                Glide.with(this).load(it).into(binding.imagePreview)
            }
        }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
            } else {
                Toast.makeText(this, "Unable to fetch location.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadStory() {
        val description = binding.etDescription.text.toString()
        if (description.isBlank() || selectedImageFile == null) {
            showError("Please add description and image!")
            return
        }

        showLoading(true)

        val fileToUpload = if (selectedImageFile!!.length() > 1 * 1024 * 1024) {
            compressImage(selectedImageFile!!)
        } else {
            selectedImageFile!!
        }

        val file = fileToUpload.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart = MultipartBody.Part.createFormData("photo", fileToUpload.name, file)
        val descriptionBody = RequestBody.create("text/plain".toMediaType(), description)

        val locationData = currentLocation?.let {
            Pair(
                RequestBody.create("text/plain".toMediaType(), it.latitude.toString()),
                RequestBody.create("text/plain".toMediaType(), it.longitude.toString())
            )
        }

        val token = storyRepository.getToken()
        if (token.isBlank()) {
            showLoading(false)
            showError("Invalid token!")
            return
        }

        val call = if (locationData != null) {
            storyRepository.uploadStoryWithLocation(
                "Bearer $token", imageMultipart, descriptionBody, locationData.first, locationData.second
            )
        } else {
            storyRepository.uploadStory("Bearer $token", imageMultipart, descriptionBody)
        }

        call.enqueue(object : retrofit2.Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: retrofit2.Call<AddNewStoryResponse>,
                response: retrofit2.Response<AddNewStoryResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(this@AddStoryActivity, "Story uploaded!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showError("Failed to upload story!")
                }
            }

            override fun onFailure(call: retrofit2.Call<AddNewStoryResponse>, t: Throwable) {
                showLoading(false)
                showError("Error: ${t.message}")
            }
        })
    }


    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var quality = 100
        val maxFileSize = 1 * 1024 * 1024

        val compressedFile = File(file.parent, "compressed_${file.name}")
        do {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val byteArray = outputStream.toByteArray()
            val fileSize = byteArray.size

            if (fileSize > maxFileSize) {
                quality -= 5
            } else {
                compressedFile.writeBytes(byteArray)
                break
            }
        } while (quality > 0)

        return compressedFile
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
