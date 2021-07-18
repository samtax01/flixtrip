package com.flixbus.flixtrip.models.requests

data class ReservationRequest(
        var tripId: Long,
        var totalSpots: Int,
        var customerName: String
    )

