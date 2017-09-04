package com.jackrockz.onboarding

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.api.UserModel
import com.jackrockz.commons.RxBaseActivity
import com.jackrockz.onboarding.fragments.SelectCountryFragment
import com.jackrockz.onboarding.fragments.WelcomeFragment
import com.jackrockz.root.MainActivity
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class WelcomeActivity : RxBaseActivity() {
    var onBackPressedListener: MainActivity.OnBackPressedListener? = null
    var callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        InitFlow()
    }

    fun InitFlow() {
        if (Utils.loadData(GlobalConstants.PREFS_IS_USER_INITIALIZED) == "true" && AccessToken.getCurrentAccessToken() != null) {
            ProcessToken(AccessToken.getCurrentAccessToken().token, true)
            return
        }

        if (Utils.loadData(GlobalConstants.PREFS_IS_USER_INITIALIZED) == "true" && Utils.loadData(GlobalConstants.PREFS_IS_TAKE_LOOK) == "true") {
            ProcessToken(isLogged = true)
            return
        }

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
            }

            override fun onError(p0: FacebookException?) {
                Utils.showToast(this@WelcomeActivity, p0?.message ?: "Unfortunately facebook login failed.")
            }

            override fun onSuccess(p0: LoginResult?) {
                Utils.showLoading(this@WelcomeActivity)
                ProcessToken(p0!!.accessToken.token)
            }
        })

        changeFragment(WelcomeFragment())
    }

    fun ProcessToken(facebook_access_token: String? = null, isLogged: Boolean = false) {
        val subscription = apiManager.getToken(facebook_access_token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { token ->
                            MyApplication.instance.accessToken = token

                            Utils.hideLoading()
                            if (isLogged) {
                                gotoNextActivity()
                            } else {
                                changeFragment(SelectCountryFragment())
                            }
                        },
                        { e ->
                            Utils.hideLoading()
                            Utils.showToast(this@WelcomeActivity, e?.message ?: "Connection Error.")
                        }
                )

        subscriptions.add(subscription)
    }

    fun changeFragment(f: Fragment, cleanStack: Boolean = false) {
        val ft = supportFragmentManager.beginTransaction()
        if (cleanStack) {
            clearBackStack()
        }
        ft.setCustomAnimations(
                R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_popup_enter, R.anim.abc_popup_exit)
        ft.add(R.id.activity_base_content, f)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun gotoNextActivity() {
        MyApplication.instance.currentUser = Utils.loadObject(GlobalConstants.PREFS_USER, UserModel::class.java)
        MyApplication.instance.UpdateUserNotificationTags()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener!!.doBack()
            return
        }

        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
