package com.jackrockz.api

import com.jackrockz.MyApplication
import com.jackrockz.utils.GlobalConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.Path
import java.util.*

class RestApi {
    private val jackApi: JackApi
    private val jackApiHeader: JackApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(GlobalConstants.API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        jackApi = retrofit.create(JackApi::class.java)

        val builder= OkHttpClient().newBuilder()
                .addInterceptor { chain ->
                    val request = chain!!.request().newBuilder().addHeader("X-JR-Token", MyApplication.instance.accessToken).build()
                    chain.proceed(request)
                }

        val retrofitHeader = Retrofit.Builder()
                .baseUrl(GlobalConstants.API_BASE_URL)
                .client(builder.build())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        jackApiHeader = retrofitHeader.create(JackApi::class.java)

    }

    fun getToken(facebook_access_token: String?) = jackApi.postUser(facebook_access_token)
    fun putMe(country: String?, city: String?, arrivalDate: Date?, departureDate: Date?, ambassadorID: String?, token: String?) = jackApiHeader.putMe(country, city, arrivalDate, departureDate, ambassadorID, token)
    fun getCities() = jackApiHeader.getCities()
    fun getEvents(id: Int, date: Date, country: String) = jackApiHeader.getEvents(id, date, country)
    fun getFeaturedEvents(id: Int, country: String) = jackApiHeader.getFeaturedEvents(id, country)
    fun getAmbassadors(code: String) = jackApiHeader.getAmbassadors(code)
    fun postPayment(id: String, eventID: String, url: String, quantity: Int?) = jackApiHeader.postPayment(id, eventID, url, quantity)
    fun getTickets() = jackApiHeader.getTickets()
    fun getTicketToken(token: String) = jackApiHeader.getTicketToken(token, token)
    fun getTicket(token: String) = jackApiHeader.getTicket(token, token)

}