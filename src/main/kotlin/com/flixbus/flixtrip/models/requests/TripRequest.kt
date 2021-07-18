package com.flixbus.flixtrip.models.requests

import java.util.*

data class TripRequest(
        var fromCity: String,
        var toCity: String,
        var startAt: Date,
        var totalSpots: Int,
        var availableSpots: Int
    )

