# Flixtrip

Spring-boot Kotlin api to reserve & cancel spots on a trip.

### Run
mvn spring-boot:run

### Swagger Spec
http://localhost:8080/swagger-ui/index.html

### Sample Request
Create Trip
- http://localhost:8080/api/admin/trips
```json
{
    "fromCity": "Berlin",
    "toCity": "Hamburg",
    "startAt": "2021-07-17",
    "totalSpot": 15,
    "availableSpot": 15
}
```

Create Reservation with the tripId.
 - http://localhost:8080/api/reservations
```json
{
    "tripId": 1,
    "totalSpot": 3,
    "customerName": "Samson Oyetola"
}
```




