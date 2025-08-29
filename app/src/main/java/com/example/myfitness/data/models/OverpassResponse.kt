// File: data/models/OverpassResponse.kt
package com.example.myfitness.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OverpassResponse(
    val version: Double,
    val generator: String,
    val osm3s: Osm3s,
    val elements: List<Element>
)

@Serializable
data class Osm3s(
    val timestampOsmBase: String? = null,
    val copyright: String
)

@Serializable
data class Element(
    val type: String,
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, JsonElement>? = null
)