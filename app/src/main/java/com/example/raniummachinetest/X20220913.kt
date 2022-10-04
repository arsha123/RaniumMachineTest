package com.example.raniummachinetest

data class X20220913(
    val absolute_magnitude_h: Double,
    val close_approach_data: List<CloseApproachDataX>,
    val estimated_diameter: EstimatedDiameterX,
    val id: String,
    val is_potentially_hazardous_asteroid: Boolean,
    val is_sentry_object: Boolean,
    val links: LinksX,
    val name: String,
    val nasa_jpl_url: String,
    val neo_reference_id: String
)