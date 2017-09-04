package com.jackrockz.commons

import android.content.Context
import android.support.v4.app.Fragment
import com.jackrockz.api.ApiManager
import rx.subscriptions.CompositeSubscription

open class RxBaseFragment() : Fragment() {
    protected var subscriptions = CompositeSubscription()
    protected val apiManager by lazy { ApiManager() }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        subscriptions = CompositeSubscription()
    }

    override fun onDetach() {
        super.onDetach()
        if (!subscriptions.isUnsubscribed) {
            subscriptions.unsubscribe()
        }
        subscriptions.clear()
    }
}