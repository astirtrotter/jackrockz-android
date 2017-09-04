package com.jackrockz.api

import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface JackApi {
    @FormUrlEncoded
    @POST("users/token")
    fun postUser(@Field("facebook_access_token") facebook_access_token: String?): Call<AuthenticationTokenModel>

    @FormUrlEncoded
    @PUT("users/me")
    fun putMe(@Field("country") country: String?, @Field("city_id") city:String?, @Field("arrival_date") arrivalDate: Date?,
              @Field("departure_data") departureDate: Date?, @Field("ambassador_id") ambassador_id: String?, @Field("facebook_access_token") token: String?): Call<UsersModel>

    @GET("cities")
    fun getCities(): Call<CitiesModel>

    @GET("cities/{id}/events/{date}")
    fun getEvents(@Path("id") id: Int, @Path("date") date: Date, @Query("target_country") country: String): Call<EventsModel>

    @GET("cities/{id}/featured_events")
    fun getFeaturedEvents(@Path("id") id: Int, @Query("target_country") country: String): Call<EventsModel>

    @GET("ambassador/{code}")
    fun getAmbassadors(@Path("code") code: String): Call<AmbassadorsModel>

    @FormUrlEncoded
    @POST("payments/events/{id}")
    fun postPayment(@Path("id") id: String, @Field("event_id") eventID: String, @Field("redirect_url") url: String, @Field("quantity") quantity: Int?): Call<Any>

    @GET("users/me/tickets")
    fun getTickets(): Call<TicketsModel>

    @GET("payments/{token}")
    fun getTicketToken(@Path("token") token: String, @Query("token") tokenQuery: String): Call<PaymentsModel>

    @GET("users/me/tickets/{token}")
    fun getTicket(@Path("token") token: String, @Query("token") tokenQuery: String): Call<OneTicketModel>
}