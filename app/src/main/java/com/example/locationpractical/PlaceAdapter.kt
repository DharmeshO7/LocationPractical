package com.example.locationpractical

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.locationpractical.databinding.ItemPlaceBinding


class PlaceAdapter(private val listener: OnPlaceListener) :
    ListAdapter<PlaceItem, PlaceAdapter.ItemViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemPlaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    inner class ItemViewHolder(private val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnDelete.setOnClickListener {
                listener.onDeleteClicked(adapterPosition, getItem(adapterPosition).placeName)
            }
        }

        fun bind(placeItem: PlaceItem) {
            binding.tvPlaceName.text = placeItem.placeName
            binding.tvPlaceAddress.text = placeItem.placeAddress

            binding.tvPlaceDistance.isVisible = if (placeItem.isPrimaryPlace) {
                binding.tvPlaceName.setTextColor(Color.RED)
                false
            } else {
                binding.tvPlaceName.setTextColor(getThemeAccentColor(binding.tvPlaceName.context))
                binding.tvPlaceDistance.text = binding.tvPlaceDistance.context.getString(
                    R.string.distance,
                    placeItem.distanceFromPrimary
                )
                true
            }
        }

        private fun getThemeAccentColor(context: Context): Int {
            val value = TypedValue()
            context.theme.resolveAttribute(android.R.attr.colorAccent, value, true)
            return value.data
        }
    }

    override fun onBindViewHolder(holder: PlaceAdapter.ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()

        currentList.removeAt(position)

        submitList(currentList) {
            listener.onPlaceRemoved(currentList.isEmpty())
        }
    }

    fun addItem(placeItem: PlaceItem) {
        val currentList = currentList.toMutableList()

        currentList.add(placeItem)

        submitList(currentList) {
            listener.onPlaceAdded()
        }
    }

    fun sortBy(isAsc: Boolean) {
        val currentList = currentList.toMutableList()

        if (isAsc) {
            currentList.sortBy { it.distanceFromPrimary }
        } else {
            currentList.sortByDescending { it.distanceFromPrimary }
        }

        submitList(currentList) {
            listener.onPlaceAdded()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PlaceItem>() {
            override fun areItemsTheSame(oldItem: PlaceItem, newItem: PlaceItem) =
                oldItem.placeId == newItem.placeId

            override fun areContentsTheSame(
                oldItem: PlaceItem,
                newItem: PlaceItem,
            ) = oldItem == newItem
        }
    }
}