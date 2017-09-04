package com.jackrockz

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.jackrockz.api.EventModel
import com.jackrockz.api.TicketModel
import com.jackrockz.api.UserModel
import com.onesignal.OneSignal
import org.json.JSONObject

//import android.support.multidex.MultiDex

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
            private set
    }

    var accessToken: String = ""
    lateinit var currentEvent: EventModel
    lateinit var currentUser: UserModel
    lateinit var currentTicket: TicketModel

    override fun onCreate() {
        super.onCreate()

        instance = this

        FacebookSdk.sdkInitialize(applicationContext)
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun UpdateUserNotificationTags() {
        val tags = JSONObject()
        tags.put("city", currentUser.city?.name)
        tags.put("country", currentUser.country)
        tags.put("locale", currentUser.locale)
        tags.put("gender", currentUser.gender)
        tags.put("arrival_date", currentUser.arrival_date)
        tags.put("departure_date", currentUser.departure_date)
        OneSignal.sendTags(tags)
    }

}