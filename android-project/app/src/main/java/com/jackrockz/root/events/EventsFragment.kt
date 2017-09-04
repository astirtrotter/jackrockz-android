package com.jackrockz.root.events

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.api.EventModel
import com.jackrockz.commons.RxBaseFragment
import com.jackrockz.root.MainActivity
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventsFragment(val isHot: Boolean = false) : RxBaseFragment(), View.OnClickListener {
    val myCalendar = Calendar.getInstance()
    val date = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        myCalendar.set(year, month, dayOfMonth, 0, 0)
        updateLabel()
    }
    val myFormat = "MMM dd, yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    var listItems = ArrayList<EventModel>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isHot) {
            btnDate.visibility = View.GONE
        }

        GetEvents()
        updateLabel()

        btnDate.setOnClickListener(this)

        recycler_view.apply {
            setHasFixedSize(true)
            val layout = GridLayoutManager(activity, 2)
            layout.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(p0: Int): Int = when(p0){
                    0 -> 2
                    else -> 1
                }
            }
            layoutManager = layout
            addItemDecoration(SpacesItemDecoration(2))
            adapter = EventsAdapter(this@EventsFragment, listItems)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnDate -> {
                val picker = DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH))
                picker.show()
            }
            else -> {
                MyApplication.instance.currentEvent = v.tag as EventModel
                startActivity(Intent(activity, EventDetailActivity::class.java))
                activity.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
            }
        }
    }

    fun updateLabel() {
        progressBar.visibility = View.VISIBLE
        listItems.clear()
        recycler_view.adapter = EventsAdapter(this, listItems)

        GetEvents()
        txtDate.text = sdf.format(myCalendar.time)
    }

    fun GetEvents() {
        if (isHot) {
            val subscription = apiManager.getFeaturedEvents(MyApplication.instance.currentUser.city!!.id, MyApplication.instance.currentUser.country).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe (
                            { aryEvents ->
                                try {
                                    progressBar.visibility = View.GONE
                                    aryEvents?.let {
                                        listItems = aryEvents as ArrayList<EventModel>
                                        recycler_view.adapter = EventsAdapter(this, listItems)
                                    }
                                } catch (ignored: Exception) {

                                }
                            },
                            { e ->
                                try {
                                    progressBar.visibility = View.GONE
                                    Utils.showToast(activity, e.message ?: "Network connection error.")
                                } catch (ignored: Exception) {

                                }
                            }
                    )
            subscriptions.add(subscription)
        } else {
            val subscription = apiManager.getEvents(MyApplication.instance.currentUser.city!!.id, myCalendar.time, MyApplication.instance.currentUser.country).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe (
                            { aryEvents ->
                                try {
                                    progressBar.visibility = View.GONE
                                    aryEvents?.let {
                                        listItems = aryEvents as ArrayList<EventModel>
                                        recycler_view.adapter = EventsAdapter(this, listItems)
                                    }
                                } catch (ignored: Exception) {

                                }
                            },
                            { e ->
                                try {
                                    progressBar.visibility = View.GONE
                                    Utils.showToast(activity, e.message ?: "Network connection error.")
                                } catch (ignored: Exception) {

                                }
                            }
                    )
            subscriptions.add(subscription)
        }
    }
}
