package com.example.locationpractical

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.locationpractical.databinding.ActivityAddPlaceBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


class AddPlaceActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mBinding: ActivityAddPlaceBinding

    private var mMap: GoogleMap? = null

    private var mCurrentMarker: Marker? = null

    private var mCurrentPlace: Place? = null

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.let {
                mCurrentPlace = Autocomplete.getPlaceFromIntent(it)

                mCurrentPlace?.let { place ->
                    place.latLng?.let { latLng ->
                        mMap?.apply {
                            mCurrentMarker?.remove()

                            mCurrentMarker = addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(place.address)
                            )

                            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F))

                            mBinding.tvSavePlaceConfirm.text =
                                getString(R.string.do_you_want_to_save_this_place, place.name)
                            mBinding.llSavePlaceConfirm.isVisible = true
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)

        listeners()
    }

    private fun listeners() {
        mBinding.btnSearchPlace.setOnClickListener {
            startAutocompleteIntent()
        }

        mBinding.btnAddPlace.setOnClickListener {
            mCurrentPlace?.let {
                val placeId = it.id!!
                val placeLatLng = it.latLng!!
                val placeName = it.name.orEmpty()
                val placeAddress = it.address.orEmpty()

                val place = PlaceItem(
                    placeId,
                    placeName,
                    placeAddress,
                    placeLatLng
                )

                val placeIntent = Intent().apply {
                    putExtra("place", place)
                }

                setResult(RESULT_OK, placeIntent)

                finish()
            }
        }
    }

    private fun startAutocompleteIntent() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setHint(getString(R.string.search_place))
            .build(this)

        startAutocomplete.launch(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val boundsIndia = LatLngBounds(LatLng(23.63936, 68.14712), LatLng(28.20453, 97.34466))
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(boundsIndia, 0)
        mMap?.animateCamera(cameraUpdate)
    }
}