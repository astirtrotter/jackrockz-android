package com.jackrockz.commons.LoopingViewPager

import android.os.Parcelable
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup


class LoopPagerAdapterWrapper internal constructor(val realAdapter: PagerAdapter) : PagerAdapter() {

    private var mToDestroy = SparseArray<ToDestroy>()

    private var mBoundaryCaching = DEFAULT_BOUNDARY_CASHING
    private var mBoundaryLooping = DEFAULT_BOUNDARY_LOOPING

    internal fun setBoundaryCaching(flag: Boolean) {
        mBoundaryCaching = flag
    }

    internal fun setBoundaryLooping(flag: Boolean) {
        mBoundaryLooping = flag
    }

    override fun notifyDataSetChanged() {
        mToDestroy = SparseArray<ToDestroy>()
        super.notifyDataSetChanged()
    }

    internal fun toRealPosition(position: Int): Int {
        var realPosition = position
        val realCount = realCount
        if (realCount == 0) return 0
        if (mBoundaryLooping) {
            realPosition = (position - 1) % realCount
            if (realPosition < 0) realPosition += realCount
        }

        return realPosition
    }

    fun toInnerPosition(realPosition: Int): Int {
        val position = realPosition + 1
        return if (mBoundaryLooping) position else realPosition
    }

    private val realFirstPosition: Int
        get() = if (mBoundaryLooping) 1 else 0

    private val realLastPosition: Int
        get() = realFirstPosition + realCount - 1

    override fun getCount(): Int {
        val count = realCount
        return if (mBoundaryLooping) count + 2 else count
    }

    val realCount: Int
        get() = realAdapter.count

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = if (realAdapter is FragmentPagerAdapter || realAdapter is FragmentStatePagerAdapter)
            position
        else
            toRealPosition(position)

        if (mBoundaryCaching) {
            val toDestroy = mToDestroy.get(position)
            if (toDestroy != null) {
                mToDestroy.remove(position)
                return toDestroy.`object`
            }
        }
        return realAdapter.instantiateItem(container, realPosition)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val realFirst = realFirstPosition
        val realLast = realLastPosition
        val realPosition = if (realAdapter is FragmentPagerAdapter || realAdapter is FragmentStatePagerAdapter)
            position
        else
            toRealPosition(position)

        if (mBoundaryCaching && (position == realFirst || position == realLast)) {
            mToDestroy.put(position, ToDestroy(container, realPosition, `object`))
        } else {
            realAdapter.destroyItem(container, realPosition, `object`)
        }
    }

    /*
     * Delegate rest of methods directly to the inner adapter.
	 */

    override fun finishUpdate(container: ViewGroup) {
        realAdapter.finishUpdate(container)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return realAdapter.isViewFromObject(view, `object`)
    }

//    override fun restoreState(bundle: Parcelable?, classLoader: ClassLoader?) {
//        realAdapter.restoreState(bundle, classLoader)
//    }

//    override fun saveState(): Parcelable {
//        return realAdapter.saveState()
//    }

    override fun startUpdate(container: ViewGroup) {
        realAdapter.startUpdate(container)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        realAdapter.setPrimaryItem(container, position, `object`)
    }

    /*
     * End delegation
	 */

    /**
     * Container class for caching the boundary views
     */
    internal class ToDestroy(var container: ViewGroup, var position: Int, var `object`: Any)

    companion object {

        private val DEFAULT_BOUNDARY_CASHING = true
        private val DEFAULT_BOUNDARY_LOOPING = true
    }
}