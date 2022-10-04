package com.example.raniummachinetest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Nasaapi {
    @GET("?api_key=I5tzcpOvEtdLYxrZb0hSCIaB3jnxVvfPgm3lyBhP")
    fun getDate(@Query("start_date") startdate: String,@Query("end_date") enddate: String): Call<ModelNasa>
}