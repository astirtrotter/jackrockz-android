package com.jackrockz.root.events

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jackrockz.R
import com.jackrockz.api.VisitorModel
import com.jackrockz.commons.extensions.inflate
import com.jackrockz.commons.extensions.loadImg
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.who_image_item.view.*

class WhoAdapter(val activity: EventDetailActivity, val items: ArrayList<VisitorModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = VisitorHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as VisitorHolder
        holder.bind(items[position])
    }

    inner class VisitorHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.who_image_item)) {
        fun bind(item: VisitorModel) = with(itemView) {
            imgView.loadImg(item.image, object: Callback {
                override fun onSuccess() {
                }

                override fun onError() {
                    items.remove(item)
                    notifyDataSetChanged()
                }

            })

            imgWho.tag = item
            imgWho.setOnClickListener (activity)
        }
    }
}