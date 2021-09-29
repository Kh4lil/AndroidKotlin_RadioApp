package com.uca.app2

import androidx.annotation.DrawableRes

data class AM(
    override val name: String,
    val frequency: Float,
    @DrawableRes override val image: Int
) : Radio(name, image)
