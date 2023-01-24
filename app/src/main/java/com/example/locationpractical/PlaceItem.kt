package com.example.locationpractical

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceItem(
    val placeId: String,
    val placeName: String,
    val placeAddress: String,
    val placeLatLng: LatLng,
) : Parcelable {
    @IgnoredOnParcel
    var isPrimaryPlace: Boolean = false

    @IgnoredOnParcel
    var distanceFromPrimary: Float = 0f
}