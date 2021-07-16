package com.flixbus.flixtrip.models.requests

data class ReservationRequest(
        var tripId: Long,
        var totalSpot: Int,
        var customerName: String
    )

