package com.flixbus.flixtrip

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition // Visit http://localhost:8080/swagger-ui.html
@SpringBootApplication
class FlixtripApplication

fun main(args: Array<String>) {
	runApplication<FlixtripApplication>(*args)
}