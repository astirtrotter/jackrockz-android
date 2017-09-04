package com.jackrockz.onboarding.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.login.LoginManager
import com.jackrockz.R
import com.jackrockz.commons.RxBaseFragment
import com.jackrockz.onboarding.WelcomeActivity
import com.jackrockz.root.MainActivity
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.fragment_select_country.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SelectCountryFragment : RxBaseFragment(), View.OnClickListener, MainActivity.OnBackPressedListener {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_select_country, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as WelcomeActivity).onBackPressedListener = this
        listOf(btnGermany, btnFrance, btnItaly, btnNetherlands, btnOther).forEach { it.setOnClickListener(this) }
    }

    override fun onDestroyView() {
        (activity as WelcomeActivity).onBackPressedListener = null
        super.onDestroyView()
    }

    override fun onClick(v: View) {
        if (v.id in listOf(R.id.btnFrance, R.id.btnGermany, R.id.btnItaly, R.id.btnNetherlands, R.id.btnOther)) {
            Utils.showLoading(activity)

            val subscription = apiManager.putMe(v.tag as String).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe (
                            { user ->
                                Utils.saveObject(GlobalConstants.PREFS_USER, user)
                                Utils.hideLoading()
                                (activity as WelcomeActivity).changeFragment(SelectCityFragment())
                            },
                            { e ->
                                Utils.hideLoading()
                                Utils.showToast(activity, e.message ?: "Network connection error.")
                            }
                    )

            subscriptions.add(subscription)
        }
    }

    override fun doBack() {
        LoginManager.getInstance().logOut()
        (activity as WelcomeActivity).onBackPressedListener = null
        activity.onBackPressed()
    }
}
