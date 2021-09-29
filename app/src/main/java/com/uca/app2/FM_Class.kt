package com.uca.app2

import androidx.annotation.DrawableRes

// This is the FM class. Each channel has a name and a frequency.
data class FM_Class(

    override val name: String,
    val frequency: Float,
    // image associated with the channel
    @DrawableRes override val image: Int
) : Radio_Class(name, image)
