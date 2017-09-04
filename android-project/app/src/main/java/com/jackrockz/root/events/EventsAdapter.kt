package com.jackrockz.root.events

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jackrockz.R
import com.jackrockz.api.EventModel
import com.jackrockz.commons.extensions.inflate
import com.jackrockz.commons.extensions.loadImg
import kotlinx.android.synthetic.main.event_item.view.*
import java.util.*

class EventsAdapter(val fragment: EventsFragment, val items: ArrayList<EventModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = EventHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as EventHolder
        holder.bind(items[position])
    }

    inner class EventHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.event_item)) {
        fun bind(item: EventModel) = with(itemView) {
            txtTitle.text = item.title
            if (item.subtitle == null) {
                txtSubTitle.visibility = View.GONE
            } else {
                txtSubTitle.text = item.subtitle
            }
            txtDescription.text = item.venue.name.toUpperCase()

            txtLeft.text = if (item.is_sold_out) {
                txtLeft.background.setColorFilter(resources.getColor(R.color.sold_out_color), PorterDuff.Mode.SRC_ATOP);
                txtLeft.setTextColor(Color.WHITE)
                "SOLD OUT"
            }
            else if (item.raw_price == 0.0)
                "FREE"
            else
                item.guestlist_count.toString() + " LEFT"

            item.image?.let{imgView.loadImg(item.image.medium)}
            tag = item
            setOnClickListener (fragment)
        }
    }
}