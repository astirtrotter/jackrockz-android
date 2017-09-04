package com.jackrockz.root.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.api.CityModel
import com.jackrockz.root.MainActivity
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils.Companion.loadObject
import com.mancj.slideup.SlideUp
import kotlinx.android.synthetic.main.contact.*
import kotlinx.android.synthetic.main.fragment_support.*

class SupportFragment : Fragment(), View.OnClickListener, MainActivity.OnBackPressedListener {
    lateinit var slideUp: SlideUp
    val city by lazy { MyApplication.instance.currentUser.city!! }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_support, container, false)
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).onBackPressedListener = this
        btnContact.setOnClickListener(this)
        txtPhone.setOnClickListener(this)
        txtCancel.setOnClickListener(this)
        txtEmail.setOnClickListener(this)
        txtWhatsApp.setOnClickListener(this)

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

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnContact -> slideUp.show()
            R.id.txtCancel -> slideUp.hide()
            R.id.txtPhone -> CallPhone()
            R.id.txtEmail -> SendEmail()
            R.id.txtWhatsApp -> RedirectWhatsApp()
        }
    }

    override fun doBack() {
        if (slideUp.isVisible) {
            slideUp.hide()
        } else {
            (activity as MainActivity).onBackPressedListener = null
            activity.onBackPressed()
        }
    }

    override fun onDestroyView() {
        (activity as MainActivity).onBackPressedListener = null
        super.onDestroyView()
    }

    fun CallPhone() {
        slideUp.hide()
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + city.phone)))
    }

    fun SendEmail() {
        slideUp.hide()
        val emailIntent = Intent(android.content.Intent.ACTION_SEND)
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
}
