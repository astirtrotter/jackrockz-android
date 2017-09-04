package com.jackrockz.api

import com.google.gson.Gson
import org.json.JSONObject
import rx.Observable
import java.util.*

class ApiManager(private val api: RestApi = RestApi()) {

    fun getToken(facebook_access_token: String?): Observable<String> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getToken(facebook_access_token)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val token = response.body().authentication_token.token

                subscriber.onNext(token)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun putMe(country: String? = null, city: String? = null, arrivalDate: Date? = null, departureDate: Date? = null, ambassadorID: String? = null, token: String? = null): Observable<UserModel> {
        return Observable.create {
            subscriber ->
            val callResponse = api.putMe(country, city, arrivalDate, departureDate, ambassadorID, token)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val user = response.body().user

                subscriber.onNext(user)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getCities(): Observable<List<CityModel>> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getCities()
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val listCities = response.body().cities

                subscriber.onNext(listCities)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getEvents(id: Int, date: Date, country: String): Observable<List<EventModel>> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getEvents(id, date, country)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val listEvents = response.body().events

                subscriber.onNext(listEvents)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getFeaturedEvents(id: Int, country: String): Observable<List<EventModel>> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getFeaturedEvents(id, country)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val listEvents = response.body().events

                subscriber.onNext(listEvents)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getAmbassador(code: String): Observable<AmbassadorModel> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getAmbassadors(code)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val ambassador = response.body().ambassador

                subscriber.onNext(ambassador)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun postPayment(id: String, eventID: String, url: String, quantity: Int?): Observable<Any> {
        return Observable.create {
            subscriber ->
            val callResponse = api.postPayment(id, eventID, url, quantity)
            val response = callResponse.execute()

            if (response.isSuccessful) {
            val jsonObject = JSONObject(response.body() as Map<*, *>)
            val output: Any
            if (jsonObject.has("payment")) {
                output= Gson().fromJson(jsonObject.toString(), PaymentsModel::class.java).payment
            } else {
                output= Gson().fromJson(jsonObject.toString(), OneTicketModel::class.java).ticket
            }

            subscriber.onNext(output)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getTickets(): Observable<List<TicketModel>> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getTickets()
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val listTickets = response.body().tickets

                subscriber.onNext(listTickets)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getTicketToken(token: String): Observable<String> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getTicketToken(token)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val ticketToken = response.body().payment.ticket_token

                subscriber.onNext(ticketToken)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getTicket(token: String): Observable<TicketModel> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getTicket(token)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val ticket = response.body().ticket

                subscriber.onNext(ticket)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }
}