package com.jackrockz.root.tickets

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jackrockz.R
import com.jackrockz.api.TicketModel
import com.jackrockz.commons.extensions.inflate
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.ticket_item.view.*
import java.util.*

class TicketsAdapter(val fragment: MyTicketsFragment, val items: ArrayList<TicketModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = TicketHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as TicketHolder
        holder.bind(items[position])
    }

    inner class TicketHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.ticket_item)) {
        fun bind(item: TicketModel) = with(itemView) {
            txtTitle.text = String.format("%s (%s)", item.event.title, item.event.venue.name)
            txtQuantity.text = String.format("%dx", item.quantity)
            txtDate.text = Utils.convertStringToDate(item.event.start_date, "MMM dd, yyyy")
            tag = item

            setOnClickListener (fragment)
        }
    }
}