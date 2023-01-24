package com.example.locationpractical

interface OnPlaceListener {
    fun onPlaceRemoved(isLast: Boolean)
    fun onPlaceAdded()
    fun onDeleteClicked(adapterPosition: Int, placeName: String)
}