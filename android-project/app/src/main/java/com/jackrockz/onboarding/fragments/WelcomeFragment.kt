package com.jackrockz.onboarding.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatTextView
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.facebook.login.LoginManager
import com.jackrockz.R
import com.jackrockz.onboarding.WelcomeActivity
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.util.*


class WelcomeFragment : Fragment(), View.OnClickListener {
    var timer: Timer? = null
    val timer_mul_iOS: Long = 1000
    val PERIOD_MS: Long = 5500 // time in milliseconds between successive task executions.
    lateinit var timerTask: TimerTask
    lateinit var backgroundTd: TransitionDrawable
    val mImages = listOf(R.drawable.welcome_background1, R.drawable.welcome_background2, R.drawable.welcome_background3)
    val mTexts = listOf("Gives you friend discounts", "Gives you fast entrance", "Gives you access to every VIP")
    var nIndex = 0
    val aniSet = AnimatorSet()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_welcome, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnFacebook.setOnClickListener(this)
        btnTakeLook.setOnClickListener(this)

        txtLoginDescription.movementMethod = LinkMovementMethod.getInstance()
        backgroundTd = TransitionDrawable(arrayOf<Drawable>(resources.getDrawable(mImages[0]), resources.getDrawable(mImages[1])))
        imgBackground.setImageDrawable(backgroundTd)

        InitBackgroundAnimation()
    }

    override fun onResume() {
        super.onResume()

        timerTask = object: TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post({ OnTimer() })
            }
        }

        timer = Timer()
        timer?.schedule(timerTask, PERIOD_MS, PERIOD_MS)

        aniSet.start()
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer?.purge()
        aniSet.cancel()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnFacebook -> LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"))
            R.id.btnTakeLook -> {
                Utils.showLoading(activity)
                Utils.saveData(GlobalConstants.PREFS_IS_TAKE_LOOK, "true")
                (activity as WelcomeActivity).ProcessToken()
            }
        }
    }

    fun OnTimer() {
        backgroundTd = TransitionDrawable(arrayOf<Drawable>(resources.getDrawable(mImages[nIndex % 3]), resources.getDrawable(mImages[(nIndex + 1) % 3])))
        imgBackground.setImageDrawable(backgroundTd)
        backgroundTd.startTransition(1000)

        nIndex++

        txtTitle.setText(mTexts[nIndex % 3])
    }

    fun InitBackgroundAnimation() {
        val translateAnimation = ObjectAnimator
                .ofFloat(imgBackground, "translationX", -60f, 60f)
                .setDuration(20 * timer_mul_iOS)

        translateAnimation.apply {
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = Animation.INFINITE
        }

        val scaleAnimationX = ObjectAnimator
                .ofFloat(imgBackground, "scaleX", 1f, 1.2f)
                .setDuration(15 * timer_mul_iOS)
        scaleAnimationX.apply {
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = Animation.INFINITE
        }

        val scaleAnimationY = ObjectAnimator
                .ofFloat(imgBackground, "scaleY", 1f, 1.2f)
                .setDuration(15 * timer_mul_iOS)
        scaleAnimationY.apply {
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = Animation.INFINITE
        }

        val animatorList = listOf(translateAnimation, scaleAnimationX, scaleAnimationY)
        aniSet.playTogether(animatorList)

        txtTitle.setFactory {
            layoutInflater.inflate(R.layout.my_welcome_text, null) as AppCompatTextView
        }

        val inAnim = AnimationUtils.loadAnimation(activity,
                android.R.anim.fade_in)
        val outAnim = AnimationUtils.loadAnimation(activity,
                android.R.anim.fade_out)
        inAnim.duration = 1000
        outAnim.duration = 1000
        txtTitle.inAnimation = inAnim
        txtTitle.outAnimation = outAnim
        txtTitle.setText(mTexts[0])
    }

}
