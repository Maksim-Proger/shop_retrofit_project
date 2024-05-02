package com.example.project.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MainApi {

    @GET("auth/products/{id}") // по id
    suspend fun getProductById(@Path("id") id: Int): Product

    @POST("auth/login") // авторизация
    suspend fun auth(@Body authRequest: AuthRequest): Response<User>

    @Headers("Content_Type: application/json")
    @GET("auth/products") // все
    suspend fun getAllProducts(@Header("Authorization") token: String) : Products








    @Headers("Content_Type: application/json")
    @GET("auth/products/search") // поиск
    suspend fun getProductsByNameAuth(
        @Header("Authorization") token: String,
        @Query("q") name: String
    ) : Products



}