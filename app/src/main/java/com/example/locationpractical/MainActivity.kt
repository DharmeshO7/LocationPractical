package com.example.locationpractical

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.locationpractical.Commons.rotateAnim
import com.example.locationpractical.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), OnPlaceListener {
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mPlaceAdapter: PlaceAdapter

    private var primaryPlace: PlaceItem? = null

    private var isAsc = false

    private val startForNav =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    private val startForAddPlace =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                @Suppress("DEPRECATION")
                val place: PlaceItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getParcelableExtra("place", PlaceItem::class.java)!!
                } else {
                    it.data?.getParcelableExtra("place")!!
                }

                primaryPlace?.let { p ->
                    val startLat = p.placeLatLng.latitude
                    val startLng = p.placeLatLng.longitude

                    val endLat = place.placeLatLng.latitude
                    val endLng = place.placeLatLng.longitude

                    val distance = FloatArray(1)

                    try {
                        Location.distanceBetween(startLat, startLng, endLat, endLng, distance)
                        place.distanceFromPrimary = distance[0] / 1000
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } ?: kotlin.run {
                    place.isPrimaryPlace = true
                    primaryPlace = place
                }

                mPlaceAdapter.addItem(place)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mPlaceAdapter = PlaceAdapter(this)

        mBinding.rvPlaces.apply {
            setHasFixedSize(true)
            adapter = mPlaceAdapter
        }

        mPlaceAdapter.submitList(mutableListOf()) {
            updateUI()
        }

        listeners()
    }

    private fun listeners() {
        mBinding.fabAddPlace.setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
            startForAddPlace.launch(intent)
        }

        mBinding.fabNav.setOnClickListener {
            if (mPlaceAdapter.currentList.size > 1) {
                val intent = Intent(this, NavActivity::class.java)
                intent.putParcelableArrayListExtra("places", ArrayList(mPlaceAdapter.currentList))
                startForNav.launch(intent)
            }
        }

        mBinding.fabSortBy.setOnClickListener {
            if (mPlaceAdapter.itemCount > 1) {
                isAsc = !isAsc
                mPlaceAdapter.sortBy(isAsc)
                it.rotateAnim(isAsc)
            }
        }
    }

    private fun updateUI() {
        if (mPlaceAdapter.itemCount > 0) showPlaces() else showNoPlaces()
    }

    private fun showPlaces() {
        mBinding.tvNoPlace.isVisible = false
        mBinding.rvPlaces.isVisible = true
    }

    private fun showNoPlaces() {
        mBinding.rvPlaces.isVisible = false
        mBinding.tvNoPlace.isVisible = true
    }

    override fun onPlaceRemoved(isLast: Boolean) {
        updateUI()

        if (isLast)
            primaryPlace = null
    }

    override fun onPlaceAdded() {
        updateUI()
    }

    override fun onDeleteClicked(adapterPosition: Int, placeName: String) {
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle("Delete Place")
            .setMessage("Are you sure you want to delete $placeName?")
            .setPositiveButton("Delete") { _, _ ->
                mPlaceAdapter.removeItem(adapterPosition)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }
}