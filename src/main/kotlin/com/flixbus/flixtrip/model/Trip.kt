package com.flixbus.flixtrip.model

import java.util.*

data class Trip(
        var id: Int,
        var fromCity: String,
        var toCity: String,
        var startAt: Date,
        val totalSpot: Int,
        val availableSpot: Int
    )

