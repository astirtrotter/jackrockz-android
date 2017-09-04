package com.jackrockz.root.events

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jackrockz.R
import com.jackrockz.api.GalleryModel
import com.jackrockz.commons.extensions.loadImg
import java.util.*

class ImagePagerAdapter(val context: Context, val items: ArrayList<GalleryModel>) : PagerAdapter() {
    override fun getCount() = items.size

    override fun isViewFromObject(view: View, obj: Any) = view === obj

    override fun destroyItem(view: ViewGroup, position: Int, `object`: Any) = view.removeView(`object` as View)

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val gallery = items[position]

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cell = inflater.inflate(R.layout.image_item, null)

        (cell.findViewById(R.id.imgView) as AppCompatImageView).loadImg(gallery.image!!.medium)

        view.addView(cell, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return cell
    }
}