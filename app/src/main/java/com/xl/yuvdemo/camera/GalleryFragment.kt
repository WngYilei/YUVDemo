package com.xl.yuvdemo.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.io.File
import android.media.MediaScannerConnection
import android.os.Build
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sheenhan.health.camera.padWithDisplayCutout
import com.sheenhan.health.camera.simulateClick

import com.xl.yuvdemo.databinding.FragmentGalleryBinding
import java.util.*

val EXTENSION_WHITELIST = arrayOf("JPG")

/** Fragment used to present the user with a gallery of photos taken */
class GalleryFragment internal constructor() : Fragment() {

    /** Android ViewBinding */
    private var _fragmentGalleryBinding: FragmentGalleryBinding? = null

    private val fragmentGalleryBinding get() = _fragmentGalleryBinding!!

    private lateinit var mediaList: MutableList<File>

    private lateinit var rootDirectory: File

    /** Adapter class used to present a fragment containing one photo or video as a page */
    @SuppressLint("WrongConstant")
    inner class MediaPagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = mediaList.size
        override fun createFragment(position: Int): Fragment =
            PhotoFragment.create(mediaList[position])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true

        // Get root directory of media from navigation arguments
        rootDirectory = CameraActivity.getOutputDirectory(requireContext())

        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.uppercase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentGalleryBinding = FragmentGalleryBinding.inflate(inflater, container, false)
        return fragmentGalleryBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Checking media files list
        if (mediaList.isEmpty()) {
            fragmentGalleryBinding.deleteButton.isEnabled = false
            fragmentGalleryBinding.shareButton.isEnabled = false
        }
        // Populate the ViewPager and implement a cache of two media items
        fragmentGalleryBinding.photoViewPager.apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }
        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
            fragmentGalleryBinding.cutoutSafeArea.padWithDisplayCutout()
        }

        // Handle delete button press
        fragmentGalleryBinding.deleteButton.simulateClick {

            mediaList.getOrNull(fragmentGalleryBinding.photoViewPager.currentItem)
                ?.let { mediaFile ->
                    mediaFile.delete()
                    MediaScannerConnection.scanFile(
                        view.context, arrayOf(mediaFile.absolutePath), null, null
                    )
                    mediaList.removeAt(fragmentGalleryBinding.photoViewPager.currentItem)
                    fragmentGalleryBinding.photoViewPager.adapter?.notifyDataSetChanged()
                    if (mediaList.isEmpty()) {
                        CameraActivity.sendEvent(CameraActivity.EVENT_CAMERA)
                    }
                }
        }


        fragmentGalleryBinding.backButton.setOnClickListener {
            CameraActivity.sendEvent(CameraActivity.EVENT_CAMERA)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mediaList = rootDirectory.listFiles { file ->
                EXTENSION_WHITELIST.contains(file.extension.uppercase(Locale.ROOT))
            }?.sortedDescending()?.toMutableList() ?: mutableListOf()
            fragmentGalleryBinding.deleteButton.isEnabled = mediaList.isNotEmpty()
            fragmentGalleryBinding.photoViewPager.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        _fragmentGalleryBinding = null
        super.onDestroyView()
    }
}
