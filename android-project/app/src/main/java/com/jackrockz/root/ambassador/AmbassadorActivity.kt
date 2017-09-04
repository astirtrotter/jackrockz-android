package com.jackrockz.root.ambassador

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.commons.RxBaseActivity
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import com.mancj.slideup.SlideUp
import kotlinx.android.synthetic.main.activity_ambassador.*
import kotlinx.android.synthetic.main.contact.*
import kotlinx.android.synthetic.main.toolbar.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AmbassadorActivity : RxBaseActivity(), View.OnClickListener {
    lateinit var slideUp: SlideUp
    val city by lazy { MyApplication.instance.currentUser.city!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ambassador)

        setSupportActionBar(toolbar)
        btnClose.visibility = View.VISIBLE

        listOf(btnClose, txtCall, txtPhone, txtEmail, txtWhatsApp, txtCancel, btnValidate).forEach { (it as View).setOnClickListener(this) }

        slideUp = SlideUp.Builder(slideView)
                .withListeners(object : SlideUp.Listener.Events {
                    override fun onSlide(percent: Float) {
                        alphaView.alpha = 1 - (percent / 100)
                    }

                    override fun onVisibilityChanged(visibility: Int) {
                        if (visibility == View.GONE) {
                        }
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withGesturesEnabled(true)
                .withLoggingEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .build()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnValidate -> OnValidate()
            R.id.btnClose -> onBackPressed()
            R.id.txtCall -> slideUp.show()
            R.id.txtCancel -> slideUp.hide()
            R.id.txtPhone -> CallPhone()
            R.id.txtEmail -> SendEmail()
            R.id.txtWhatsApp -> RedirectWhatsApp()
        }
    }

    override fun onBackPressed() {
        if (slideUp.isVisible) {
            slideUp.hide()
        } else {
            super.onBackPressed()
        }
    }

    fun CallPhone() {
        slideUp.hide()
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + city.phone)))
    }

    fun SendEmail() {
        slideUp.hide()
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "nessage/rfc822"
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf<String>(city.email))
        startActivity(Intent.createChooser(emailIntent, "Send mail..."))
    }

    fun RedirectWhatsApp() {
        slideUp.hide()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=" + city.phone)
        startActivity(intent)
    }

    fun OnValidate() {
        Utils.showLoading(this)
        val subscription = apiManager.getAmbassador(txtCode.text.toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { ambassador ->
                            val subscription = apiManager.putMe(ambassadorID = ambassador.id.toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe (
                                            { user ->
                                                MyApplication.instance.currentUser = user
                                                MyApplication.instance.UpdateUserNotificationTags()
                                                Utils.saveObject(GlobalConstants.PREFS_USER, user)

                                                Utils.hideLoading()
                                                onBackPressed()
                                            },
                                            { e ->
                                                Utils.hideLoading()
                                                Utils.showAlertDialog(this, message = e.message ?: "Connection Error")
                                            }
                                    )
                            subscriptions.add(subscription)
                        },
                        { e ->
                            Utils.hideLoading()
                            Utils.showAlertDialog(this, "Oops...", "The entered code is invalid. Please enter the correct code.")
                        }
                )
        subscriptions.add(subscription)
    }
}
