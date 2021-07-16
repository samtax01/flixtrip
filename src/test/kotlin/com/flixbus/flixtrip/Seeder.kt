package com.flixbus.flixtrip

import com.flixbus.flixtrip.models.requests.ReservationRequest
import com.flixbus.flixtrip.models.requests.TripRequest
import java.util.*

class Seeder {


    companion object{
        fun getTripRequest(totalSpot: Int = 10): TripRequest{
            return TripRequest(
                fromCity = "Berlin",
                toCity = "Hamburg",
                startAt = Date(),
                totalSpot = totalSpot,
                availableSpot = totalSpot,
            );
        }


        fun getReservationRequest(tripId: Long, totalSpot: Int = 2): ReservationRequest{
            return ReservationRequest(
                tripId = tripId,
                totalSpot = totalSpot,
                customerName = "Samson Oyetola",
            )
        }


    }
}