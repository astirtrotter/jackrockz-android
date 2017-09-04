package com.jackrockz.commons.LoopingViewPager

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet


class LoopViewPager : ViewPager {

    private var mAdapter: LoopPagerAdapterWrapper? = null
    private var mBoundaryCaching = DEFAULT_BOUNDARY_CASHING
    private var mBoundaryLooping = DEFAULT_BOUNDARY_LOOPING
    private var mOnPageChangeListeners: MutableList<OnPageChangeListener>? = null

    /**
     * If set to true, the boundary views (i.e. first and last) will never be
     * destroyed This may help to prevent "blinking" of some views
     */
    fun setBoundaryCaching(flag: Boolean) {
        mBoundaryCaching = flag
        if (mAdapter != null) {
            mAdapter!!.setBoundaryCaching(flag)
        }
    }

    fun setBoundaryLooping(flag: Boolean) {
        mBoundaryLooping = flag
        if (mAdapter != null) {
            mAdapter!!.setBoundaryLooping(flag)
        }
    }

    override fun setAdapter(adapter: PagerAdapter) {
        mAdapter = LoopPagerAdapterWrapper(adapter)
        mAdapter!!.setBoundaryCaching(mBoundaryCaching)
        mAdapter!!.setBoundaryLooping(mBoundaryLooping)
        super.setAdapter(mAdapter)
        setCurrentItem(0, false)
    }

    override fun getAdapter(): PagerAdapter {
        return if (mAdapter != null) mAdapter!!.realAdapter else mAdapter!!
    }

    override fun getCurrentItem(): Int {
        return if (mAdapter != null) mAdapter!!.toRealPosition(super.getCurrentItem()) else 0
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        val realItem = mAdapter!!.toInnerPosition(item)
        super.setCurrentItem(realItem, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        if (currentItem != item) {
            setCurrentItem(item, true)
        }
    }

    override fun setOnPageChangeListener(listener: OnPageChangeListener) {
        addOnPageChangeListener(listener)
    }

    override fun addOnPageChangeListener(listener: OnPageChangeListener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = ArrayList()
        }
        mOnPageChangeListeners!!.add(listener)
    }

    override fun removeOnPageChangeListener(listener: OnPageChangeListener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners!!.remove(listener)
        }
    }

    override fun clearOnPageChangeListeners() {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners!!.clear()
        }
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        if (onPageChangeListener != null) {
            super.removeOnPageChangeListener(onPageChangeListener)
        }
        super.addOnPageChangeListener(onPageChangeListener)
    }

    private val onPageChangeListener = object : OnPageChangeListener {
        private var mPreviousOffset = -1f
        private var mPreviousPosition = -1f

        override fun onPageSelected(position: Int) {

            val realPosition = mAdapter!!.toRealPosition(position)
            if (mPreviousPosition != realPosition.toFloat()) {
                mPreviousPosition = realPosition.toFloat()

                if (mOnPageChangeListeners != null) {
                    for (i in mOnPageChangeListeners!!.indices) {
                        val listener = mOnPageChangeListeners!![i]
                        listener?.onPageSelected(realPosition)
                    }
                }
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            var realPosition = position
            if (mAdapter != null) {
                realPosition = mAdapter!!.toRealPosition(position)

                if (positionOffset == 0f && mPreviousOffset == 0f && (position == 0 || position == mAdapter!!.getCount() - 1)) {
                    setCurrentItem(realPosition, false)
                }
            }

            mPreviousOffset = positionOffset

            if (mOnPageChangeListeners != null) {
                for (i in mOnPageChangeListeners!!.indices) {
                    val listener = mOnPageChangeListeners!![i]
                    if (listener != null) {
                        if (realPosition != mAdapter!!.realCount - 1) {
                            listener.onPageScrolled(realPosition, positionOffset,
                                    positionOffsetPixels)
                        } else {
                            if (positionOffset > .5) {
                                listener.onPageScrolled(0, 0f, 0)
                            } else {
                                listener.onPageScrolled(realPosition, 0f, 0)
                            }
                        }
                    }
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (mAdapter != null) {
                val position = super@LoopViewPager.getCurrentItem()
                val realPosition = mAdapter!!.toRealPosition(position)
                if (state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == mAdapter!!.getCount() - 1)) {
                    setCurrentItem(realPosition, false)
                }
            }

            if (mOnPageChangeListeners != null) {
                for (i in mOnPageChangeListeners!!.indices) {
                    val listener = mOnPageChangeListeners!![i]
                    listener?.onPageScrollStateChanged(state)
                }
            }
        }
    }

    companion object {
        private val DEFAULT_BOUNDARY_CASHING = false
        private val DEFAULT_BOUNDARY_LOOPING = true

        /**
         * helper function which may be used when implementing FragmentPagerAdapter

         * @return (position-1)%count
         */
        fun toRealPosition(position: Int, count: Int): Int {
            var position = position
            position = position - 1
            if (position < 0) {
                position += count
            } else {
                position = position % count
            }
            return position
        }
    }
}