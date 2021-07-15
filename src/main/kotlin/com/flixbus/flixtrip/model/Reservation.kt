package com.flixbus.flixtrip.model

data class Reservation(
        val id: Int,
        val tripId: Int,
        val totalSpot: Int,
        val customerName: String
    )

