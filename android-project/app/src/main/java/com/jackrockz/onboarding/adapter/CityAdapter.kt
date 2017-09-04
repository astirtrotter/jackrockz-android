package com.jackrockz.onboarding.adapter;

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jackrockz.R
import com.jackrockz.api.CityModel
import com.jackrockz.commons.extensions.inflate
import com.jackrockz.commons.extensions.loadImg
import com.jackrockz.onboarding.fragments.SelectCityFragment
import kotlinx.android.synthetic.main.city_item.view.*
import java.util.*

class CityAdapter(val fragment: SelectCityFragment, val items: ArrayList<CityModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int): RecyclerView.ViewHolder = CityHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as CityHolder
        holder.bind(items[position])
    }

    inner class CityHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.city_item)) {
        fun bind(item: CityModel) = with(itemView) {
            txtTitle.text = item.name
            item.image?.let { imgView.loadImg(item.image.medium) }

            tag = item
            setOnClickListener (fragment)
        }
    }
}