package com.jackrockz.utils

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class TapOpacityHighlightLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isSemiAlpha = false

    override fun onInterceptTouchEvent(ev: MotionEvent?) = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return true

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                animateAlpha()
                isSemiAlpha = true
            }

            MotionEvent.ACTION_MOVE -> {
                if (0 <= event.x && event.x < width && 0 <= event.y && event.y < height) {
                    if (!isSemiAlpha) {
                        animateAlpha()
                        isSemiAlpha = true
                    }
                } else if (isSemiAlpha) {
                    animateAlpha(1f)
                    isSemiAlpha = false
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isSemiAlpha) {
                    animateAlpha(1f)
                    if (isClickable)
                        performClick()
                    else
                        getChildAt(0).callOnClick()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isSemiAlpha) {
                    animateAlpha(1f)
                }
            }
        }

        return true
    }
}
/**
 * Will be used to animate on click event
 */
fun View.animateAlpha(alpha: Float = 0.5f, duration: Long = 200) {
    ViewCompat.animate(this).alpha(alpha).setDuration(duration).start()
}
