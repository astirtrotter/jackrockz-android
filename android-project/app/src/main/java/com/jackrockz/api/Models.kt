package com.jackrockz.api

// Authentication Token Model
class AuthenticationTokenModel (
        val authentication_token: TokenModel
)
class TokenModel (
        val token: String
)

//City Model
class CitiesModel(
        val cities: List<CityModel>
)
class CityModel(
        val id: Int,
        val name: String,
        val phone: String,
        val email: String,
        val image: HashImage?
)
class HashImage(
        val default: String,
        val square: String,
        val medium: String,
        val large: String
)


//Event Model
class EventsModel(
        val events: List<EventModel>
)
class EventModel(
        val id: Int,
        val title: String,
        val subtitle: String?,
        val description: String,
        val guestlist_count: Int,
        val views_count: Int,
        val experience_count: Int,
        val image: HashImage?,
        val price: String,
        val regular_price: String,
        val venue: VenueModel,
        val start_date: String,
        val end_date: String,
        val raw_price: Double,
        val raw_prepayment_price: Double,
        val gallery: GalleriesModel?,
        val visitors: List<VisitorModel>?,
        val reviews: List<ReviewModel>,
        val note: String?,
        val is_sold_out: Boolean,
        val supplementary_subtitle: String
)
class VenueModel(
        val name: String,
        val city: String,
        val address: String?,
        val latitude: Double?,
        val longitude: Double?
)
class GalleriesModel(
        val items: List<GalleryModel>?
)
class GalleryModel(
        val image: HashImage?
)
class VisitorModel(
        val first_name: String,
        val last_name: String,
        val link: String,
        val image: String
)
class ReviewModel(
        val name: String,
        val body: String,
        val rating: Int,
        val link: String?,
        val image: HashImage
)


//Ambassador Model
class AmbassadorsModel(
        val ambassador: AmbassadorModel
)
class AmbassadorModel(
        val id: Int,
        val first_name: String,
        val last_name: String,
        val code: String
)


//User Model
class UserModel(
        val id: Int,
        val first_name: String?,
        val last_name: String?,
        val email: String?,
        val locale: String,
        val country: String,
        val arrival_date: String?,
        val departure_date: String?,
        val gender: String?,
        val city: CityModel?,
        val ambassador: AmbassadorModel?
)
class UsersModel(
        val user: UserModel
)


//Payment Model
data class PaymentsModel(
        val payment: PaymentModel
)
data class PaymentModel(
        val token: String,
        val payment_url: String,
        val total: String,
        val ticket_token: String?
)


//Ticket Model
class TicketsModel(
        val tickets: List<TicketModel>
)
class TicketModel(
        val token: String,
        val first_name: String,
        val last_name: String,
        val email: String,
        val quantity: Int,
        val price: String,
        val total: String,
        val paid_amount: String,
        val open_amount: String,
        val checkin_url: String,
        val event: EventModel

)
class OneTicketModel(
        val ticket: TicketModel
)