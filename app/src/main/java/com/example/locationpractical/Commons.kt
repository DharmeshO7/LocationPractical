package com.example.locationpractical

import android.view.View

object Commons {
    fun View.rotateAnim(isAsc: Boolean) {
        val anim = animate()

        if (isAsc)
            anim.rotationX(180f)
        else
            anim.rotationX(0f)

        anim.start()
    }
}