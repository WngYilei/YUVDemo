package com.xl.yuvdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import java.io.File
import androidx.lifecycle.*
import com.xl.yuvdemo.camera.CameraFragment
import com.xl.yuvdemo.camera.GalleryFragment
import com.xl.yuvdemo.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraBinding
    private val cameraFragment = CameraFragment()
    private val galleryFragment = GalleryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .add(R.id.container, cameraFragment)
            .add(R.id.container, galleryFragment)
            .commit()


    }

    override fun onResume() {
        super.onResume()
        switchCamera()
    }


    private fun switchCamera() {
        supportFragmentManager.beginTransaction()
            .show(cameraFragment)
            .hide(galleryFragment)
            .commit()
    }

    private fun switchGallery() {
        supportFragmentManager.beginTransaction()
            .show(galleryFragment)
            .hide(cameraFragment)
            .commit()
    }

    companion object {

        val event = MutableLiveData<Int>()
        const val EVENT_CAMERA = 1
        const val EVENT_GALLERY = 2
        const val EVENT_TAKE_PHOTO = 3
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }



        fun sendEvent(eventMsg: Int) {
            event.postValue(eventMsg)
        }

    }


}