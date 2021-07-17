package com.flixbus.flixtrip

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry // To retry an action on Exception (Used with OptimisticLockException in the code)
@OpenAPIDefinition // Visit http://localhost:8080/swagger-ui.html
@SpringBootApplication
class FlixtripApplication

fun main(args: Array<String>) {
	runApplication<FlixtripApplication>(*args)
}