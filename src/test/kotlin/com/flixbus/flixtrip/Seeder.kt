package com.flixbus.flixtrip

import com.flixbus.flixtrip.models.requests.ReservationRequest
import com.flixbus.flixtrip.models.requests.TripRequest
import java.time.temporal.ChronoUnit
import java.util.*

class Seeder {


    companion object{

        /**
         * Sample Trip Request
         */
        fun getTripRequest(totalSpot: Int = 10): TripRequest{
            return TripRequest(
                fromCity = "Berlin",
                toCity = "Hamburg",
                startAt = Date.from(Date().toInstant().plus(5, ChronoUnit.DAYS)),
                totalSpots = totalSpot,
                availableSpots = totalSpot,
            );
        }


        /**
         * Sample Reservation Request
         */
        fun getReservationRequest(tripId: Long, totalSpot: Int = 2): ReservationRequest{
            return ReservationRequest(
                tripId = tripId,
                totalSpots = totalSpot,
                customerName = "Samson Oyetola",
            )
        }

    }
}