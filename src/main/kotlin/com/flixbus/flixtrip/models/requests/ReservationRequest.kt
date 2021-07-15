package com.flixbus.flixtrip.models.requests

data class ReservationRequest(
        val tripId: Int,
        val totalSpot: Int,
        val customerName: String
    )

